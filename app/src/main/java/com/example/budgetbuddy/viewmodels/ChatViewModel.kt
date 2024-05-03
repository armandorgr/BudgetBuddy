package com.example.budgetbuddy.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.R
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val usersRepository: UsersRepository,
    private val messagesRepository: MessageRepository,
    private val storageRepository: StorageRepository,
) : ViewModel() {
    private val _isMember: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var isMember: StateFlow<Boolean> = _isMember
    private var _messages: MutableStateFlow<List<ListItemUiModel.MessageUiModel>> =
        MutableStateFlow(emptyList())
    val messages: StateFlow<List<ListItemUiModel.MessageUiModel>> = _messages
    private var valueEventAdded: Boolean = false
    private var messageText: String = ""
    private var childEventsAdded: Boolean = false

    private val messagesChildEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val message = snapshot.getValue(Message::class.java)
            val key = snapshot.key
            var senderData: User? = null
            message?.senderUID?.let {
                usersRepository.findUserByUIDNotSuspend(it).addOnSuccessListener { data ->
                    senderData = data.getValue(User::class.java)
                    if (key != null) {
                        val messageUiModel = ListItemUiModel.MessageUiModel(key, message, senderData)
                        addMessage(messageUiModel)
                    }
                }
            }
        }

        private fun addMessage(message: ListItemUiModel.MessageUiModel) {
            val updatedList = _messages.value.toMutableList().apply {
                add(message)
            }
            _messages.value = updatedList
        }

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

    private val memberShipEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val memberShipData = snapshot.getValue(ROLE::class.java)
            _isMember.value = memberShipData != null
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "onCancelled chatViewModel")
        }
    }

    fun validateMessage(context: Context): String? {
        val validator = MessageValidator(context)
        return validator.validate(this.messageText)
    }

    fun setMessageText(message: String) {
        this.messageText = message
    }

    fun addMemberShipListener(groupUID: String, userUID: String) {
        if (valueEventAdded) return
        groupRepository.addMemberShipListener(groupUID, userUID, memberShipEventListener)
        valueEventAdded = true
    }

    fun sendMessage(groupUID: String, userUID: String) {
        val message = Message(this.messageText, userUID, null, MESSAGE_TYPE.TEXT)
        messagesRepository.writeNewMessage(groupUID, message)
    }

    fun onSuccessGallery(
        uri: Uri,
        groupUID: String,
        userUID: String,
        onComplete: (Task<Void>) -> Unit
    ) {
        val key = messagesRepository.getNewKey(groupUID)
        key?.let {
            storageRepository.saveImageFromUri(uri, key).addOnCompleteListener {
                if (it.isSuccessful) {
                    val message = Message(null, userUID, null, MESSAGE_TYPE.IMAGE)
                    messagesRepository.writeNewImageMessage(groupUID, key, message)
                        .addOnCompleteListener(onComplete)
                }
            }
        }
    }

    fun onSuccessCamera(
        bitmap: Bitmap,
        groupUID: String,
        userUID: String,
        onComplete: (Task<Void>) -> Unit
    ) {
        val key = messagesRepository.getNewKey(groupUID)
        key?.let {
            storageRepository.saveImageFromBitmap(bitmap, key).addOnCompleteListener {
                if (it.isSuccessful) {
                    val message = Message(null, userUID, null, MESSAGE_TYPE.IMAGE)
                    messagesRepository.writeNewImageMessage(groupUID, key, message)
                        .addOnCompleteListener(onComplete)
                }
            }
        }
    }

    fun onPhotoLoadFail(context: Context) {
        Toast.makeText(context, "Error cargando foto", Toast.LENGTH_SHORT).show()
    }

    fun loadMessages(groupUID: String) {
        if (childEventsAdded) return
        messagesRepository.addChildEventListener(groupUID, messagesChildEventListener)
        childEventsAdded = true
    }
}