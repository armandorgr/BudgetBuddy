package com.example.budgetbuddy.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.R
import com.example.budgetbuddy.http.FcmAPI
import com.example.budgetbuddy.http.NotificationBody
import com.example.budgetbuddy.http.SendMessageRequest
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.MESSAGE_TYPE
import com.example.budgetbuddy.model.Message
import com.example.budgetbuddy.model.ROLE
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.GroupRepository
import com.example.budgetbuddy.repositories.MessageRepository
import com.example.budgetbuddy.repositories.StorageRepository
import com.example.budgetbuddy.repositories.UsersRepository
import com.example.budgetbuddy.validations.validators.MessageValidator
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * ViewModel en el cual de define toda la lógica relacionada al fragmento de chat
 * @param groupRepository Repositorio de grupos
 * @param usersRepository Repositorio de usuarios
 * @param messagesRepository Repositorio de mensajes
 * @param storageRepository Repositorio para el almacenamiento de Firebase Storage
 * Uso de [ViewModel] consultado aquí:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Android Architecture Components - ViewModel
 *
 * @author Armando Guzmán
 * */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val usersRepository: UsersRepository,
    private val messagesRepository: MessageRepository,
    private val storageRepository: StorageRepository,
    private val notificationService: FcmAPI
) : ViewModel() {
    private val _isMember: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var isMember: StateFlow<Boolean> = _isMember
    private var _messages: MutableStateFlow<List<ListItemUiModel.MessageUiModel>> =
        MutableStateFlow(emptyList())
    val messages: StateFlow<List<ListItemUiModel.MessageUiModel>> = _messages
    private var valueEventAdded: Boolean = false
    private var messageText: String = ""
    private var childEventsAdded: Boolean = false

    /**
     * Objeto anonimo que implementa la interfaz [ChildEventListener] usado para
     * cargar los mensajes desde la base de datos
     * */
    private val messagesChildEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val message = snapshot.getValue(Message::class.java)
            val key = snapshot.key
            var senderData: User? = null
            message?.senderUID?.let {
                usersRepository.findUserByUIDNotSuspend(it).addOnSuccessListener { data ->
                    senderData = data.getValue(User::class.java)
                    if (key != null) {
                        val messageUiModel =
                            ListItemUiModel.MessageUiModel(key, message, senderData)
                        addMessage(messageUiModel)
                    }
                }
            }
        }

        /**
         * Método que sirve para añadir un mensaje a la lista de mensaje cargados
         * @param message Mensaje a añadir
         * */
        private fun addMessage(message: ListItemUiModel.MessageUiModel) {
            val updatedList = _messages.value.toMutableList().apply {
                add(message)
            }
            _messages.value = updatedList
        }

        /**
         * Método que sirve para eliminar un mensaje a partir de su UID
         * @param uid UID del mensaje a eliminar
         * */
        private fun deleteMessage(uid: String) {
            val updatedList = _messages.value.toMutableList().apply {
                removeIf { message -> message.uid == uid }
            }
            _messages.value = updatedList
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("prueba", "onChildChanged ChatViewModel")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val key = snapshot.key
            key?.let { deleteMessage(it) }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("prueba", "onChildMoved ChatViewModel")

        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "onCancelled ChatViewModel")
        }

    }

    /**
     * Objeto anónimo que implementa la interfaz [ValueEventListener] usado para estar al tanto
     * de si el usuario actual sigue siendo miembro o no del grupo
     * */
    private val memberShipEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val memberShipData = snapshot.getValue(ROLE::class.java)
            _isMember.value = memberShipData != null
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "onCancelled chatViewModel")
        }
    }

    /**
     * Método que sirve para validar el mensaje escrito por el usuario antes de
     * enviarlo a la base de datos
     * @param context Contexto usado para acceder a los recursos de la aplicación y obtener
     * el mensaje localizado de error
     *  @return El mensaje de error si el mensaje no cumple la validación, o null si el mensaje
     *  es correcto
     * */
    fun validateMessage(context: Context): String? {
        val validator = MessageValidator(context)
        return validator.validate(this.messageText)
    }

    /**
     * Método que sirve para establecer la variable en donde se almacena
     * el mensaje actualmente escrito por el usuario
     * @param message Mensaje a guardar
     * */
    fun setMessageText(message: String) {
        this.messageText = message
    }

    /**
     * Método que sirve para añadir el listener que estará pendiente de si el usuario sigue
     * siendo miembro del grupo o no
     * @param groupUID UID del grupo del cual se quiere saber si se sigue siendo miembro o no
     * @param userUID UID del usuario del cual se estará pendiente dentro del grupo
     * */
    fun addMemberShipListener(groupUID: String, userUID: String) {
        if (valueEventAdded) return
        groupRepository.addMemberShipListener(groupUID, userUID, memberShipEventListener)
        valueEventAdded = true
    }

    /**
     * Método que sirve para enviar un mensaje nuevo
     * @param groupUID UID del grupo en donde enviar el mensaje
     * @param userUID UID del usuario que envía el mensaje
     * */
    fun sendMessage(groupUID: String, userUID: String, context: Context) {
        val message = Message(this.messageText, userUID, null, MESSAGE_TYPE.TEXT)
        viewModelScope.launch {
            val username = usersRepository.findUserByUID(userUID)?.username ?: context.getString(R.string.user)
            val groupName = groupRepository.findGroupByUID(groupUID).await().getValue(Group::class.java)?.name
            messagesRepository.writeNewMessage(groupUID, message){ groupUID ->
                try{
                    notificationService.sendMessage(
                        SendMessageRequest(
                            to = groupUID,
                            NotificationBody(
                                title = groupName ?: "Nuevo mensaje",
                                body = "$username: ${message.text!!}"
                            )
                        )
                    ).execute()
                }catch (e: Exception){
                    Log.d("prueba", e.message.toString())
                }
            }
        }
    }

    /**
     * Método que sirve para guardar un mensaje con foto cargada desde la galería
     * @param uri Uri de la foto cargada
     * @param groupUID UID del grupo en donde se guardará el mensaje
     * @param userUID UID del usuario que envía el mensaje
     * @param onComplete Función que se llamará al finalizar la tarea de guardar el mensaje
     * */
    fun onSuccessGallery(
        uri: Uri,
        groupUID: String,
        userUID: String,
        context: Context,
        onComplete: (Task<Void>) -> Unit
    ) {
        val key = messagesRepository.getNewKey(groupUID)
        key?.let {
            storageRepository.saveImageFromUri(uri, key).addOnCompleteListener {
                if (it.isSuccessful) {
                    val message = Message(null, userUID, null, MESSAGE_TYPE.IMAGE)
                    messagesRepository.writeNewImageMessage(groupUID, key, message)
                        .addOnCompleteListener{task ->
                            viewModelScope.launch(Dispatchers.IO){
                                try{
                                    val groupName = groupRepository.findGroupByUID(groupUID).await().getValue(Group::class.java)?.name
                                    val username = usersRepository.findUserByUID(userUID)?.username ?: context.getString(R.string.user)
                                    notificationService.sendMessage(
                                        SendMessageRequest(
                                            to = groupUID,
                                            NotificationBody(
                                                title = groupName ?: context.getString(R.string.new_message),
                                                body = context.getString(R.string.message_body_image, username)
                                            )
                                        )
                                    ).execute()
                                    Log.d("Prueba", groupName!!);
                                    Log.d("Prueba", username);
                                }catch (e: Exception){
                                    Log.d("prueba", e.message.toString())
                                }
                            }
                            onComplete(task)
                        }
                }
            }
        }
    }

    /**
     * Método que sirve para guardar un mensaje con foto cargada desde la cámara
     * @param bitmap Bitmap de la foto cargada
     * @param groupUID UID del grupo en donde se guardará el mensaje
     * @param userUID UID del usuario que envía el mensaje
     * @param onComplete Función que se llamará al finalizar la tarea de guardar el mensaje
     * */
    fun onSuccessCamera(
        bitmap: Bitmap,
        groupUID: String,
        userUID: String,
        context: Context,
        onComplete: (Task<Void>) -> Unit
    ) {
        val key = messagesRepository.getNewKey(groupUID)
        key?.let {
            storageRepository.saveImageFromBitmap(bitmap, key).addOnCompleteListener {
                if (it.isSuccessful) {
                    val message = Message(null, userUID, null, MESSAGE_TYPE.IMAGE)
                    messagesRepository.writeNewImageMessage(groupUID, key, message)
                        .addOnCompleteListener{task->
                            viewModelScope.launch(Dispatchers.IO){
                                try{
                                    val groupName = groupRepository.findGroupByUID(groupUID).await().getValue(Group::class.java)?.name
                                    val username = usersRepository.findUserByUID(userUID)?.username ?: context.getString(R.string.user)
                                    notificationService.sendMessage(
                                        SendMessageRequest(
                                            to = groupUID,
                                            NotificationBody(
                                                title = groupName ?: context.getString(R.string.new_message),
                                                body = context.getString(R.string.message_body_image, username)
                                            )
                                        )
                                    ).execute()
                                    Log.d("Prueba", groupName!!);
                                    Log.d("Prueba", username);
                                }catch (e: Exception){
                                    Log.d("prueba", e.message.toString())
                                }
                            }
                            onComplete(task)
                        }
                }
            }
        }
    }

    /**
     * Método que se llamará al fallar la carga de una foto ya sea desde la galería o la cámara
     * @param context Contexto usado para mostrar un [Toast] y acceder a los recursos de la aplicación
     * y obtener el mensaje de error localizado
     * */
    fun onPhotoLoadFail(context: Context) {
        Toast.makeText(
            context,
            context.getString(R.string.error_loading_picture),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Método que sirve para cargar los mensajes pertenecientes al grupo
     * @param groupUID UID del grupo del cual cargar los mensajes
     * */
    fun loadMessages(groupUID: String) {
        if (childEventsAdded) return
        messagesRepository.addChildEventListener(groupUID, messagesChildEventListener)
        childEventsAdded = true
    }
}