package com.example.budgetbuddy.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.NewGroupFriendsAdapter
import com.example.budgetbuddy.model.GROUP_CATEGORY
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.ROLE
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.GroupRepository
import com.example.budgetbuddy.repositories.StorageRepository
import com.example.budgetbuddy.repositories.UsersRepository
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.DateResult
import com.example.budgetbuddy.util.Utilities
import com.example.budgetbuddy.validations.validators.GroupDescriptionValidator
import com.example.budgetbuddy.validations.validators.GroupNameValidator
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel en el cual de define la lógica para todas las acciones relacionadas con un grupo
 * @param repo Repositorio de grupo
 * @param userRepo Repositorio de usuarios
 * @param storageRepository Repositorio de Firebase Storage
 * Uso de [ViewModel] consultado aquí:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Android Architecture Components - ViewModel
 *  La forma de trabajar con la autenticación de Firebase fue consultada en la documentacion de Firebase: https://firebase.google.com/docs/auth/android/start
 * @author Armando Guzmán
 * */
@HiltViewModel
class NewGroupViewModel @Inject constructor(
    private val repo: GroupRepository,
    private val userRepo: UsersRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {
    private var childEventsAdded = false
    private var _currentUserUid: String? = null
    private val currentUserUid get() = _currentUserUid!!
    private var groupPhoto: Any? = null
    private val startDateLimit = LocalDateTime.of(2000, 1, 1, 0, 0)
    private val endDateLimit = LocalDateTime.of(2030, 1, 1, 0, 0)
    private val selectedUsers: MutableList<ListItemUiModel.User> = mutableListOf()
    private val _members: MutableStateFlow<List<ListItemUiModel.User>> =
        MutableStateFlow(emptyList())
    val members: StateFlow<List<ListItemUiModel.User>> = _members
    private var _groupCategory = GROUP_CATEGORY.UNDEFINED
    val groupCategory = _groupCategory

    private val _currentUserRole: MutableStateFlow<ROLE> = MutableStateFlow(ROLE.MEMBER)
    val currentUserRole: StateFlow<ROLE> = _currentUserRole

    private val _startDate = MutableLiveData<LocalDateTime?>()
    val startDate: LiveData<LocalDateTime?> = _startDate

    private val _endDate = MutableLiveData<LocalDateTime?>()
    val endDate: LiveData<LocalDateTime?> = _endDate

    private val _groupName: MutableLiveData<String> = MutableLiveData<String>()
    val groupName: LiveData<String> = _groupName

    private val _groupDescription = MutableLiveData<String>()
    val groupDescription: LiveData<String> = _groupDescription

    private val _groupNameError = MutableLiveData<String?>()
    val groupNameError: LiveData<String?> = _groupNameError

    private val _groupDescriptionError = MutableLiveData<String?>()
    val groupDescriptionError: LiveData<String?> = _groupDescriptionError

    private var onCurrentUserBanned: (() -> Unit)? = null

    fun setGroupCategory(category: GROUP_CATEGORY) {
        this._groupCategory = category
    }

    fun setCurrentUserUID(uid: String) {
        this._currentUserUid = uid
    }

    fun setGroupPhoto(photo: Any?) {
        this.groupPhoto = photo
    }

    fun getGroupPhoto(): Any? {
        return this.groupPhoto
    }

    /**
     * Método que sirve para dejar un grupo
     * @param groupUID UID del grupo que se quiere abandonar
     * @param onCompleteListener Función que se llama al terminar la tarea de dejar el grupo
     * dentro de la base de datos
     * */
    fun leaveGroup(groupUID: String, onCompleteListener: (task: Task<Void>) -> Unit) {
        repo.leaveGroup(currentUserUid, groupUID, onCompleteListener)
    }

    /**
     * Método que sirve para obtener la fecha seleccionada en un [DatePicker]
     * @param datePicker DatePicker del cual se quiere extraer la fecha seleccionada
     * @return La fecha seleccionada en el [DatePicker] en forma de un objeto [LocalDateTime]
     * */
    private fun getDate(datePicker: DatePicker): LocalDateTime {
        return LocalDateTime.of(
            datePicker.year,
            datePicker.month + 1,
            datePicker.dayOfMonth,
            0,
            0,
            0
        )
    }

    /**
     * Método que sirve para mostrar un [Toast]
     * @param message Mensaje a mostrar en el [Toast]
     * @param context Contexto usado para mostrar el [Toast]
     * */
    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Método que se llamará al pulsar sobre el input para escoger la fecha fin del grupo en la vista
     * Este mostrará una ventana emergente desde donde el usuario podrá escoger una fecha
     * @param context Contexto usado para mostrar la ventana emergente
     * @param view Vista en donde se mostrará la ventana emergente
     * */
    fun onEndDateClick(context: Context, view: View) {
        val dateResult = DateResult(
            context.getString(R.string.end_date_title),
            {
                val datePicker = it.findViewById<DatePicker>(R.id.datePicker)
                val response: String? = validateEndDate(getDate(datePicker), context)
                if (response != null) {
                    showToast(response, context)
                }
            }, {}
        )
        showDatePickerDialog(context, dateResult, view)
    }

    /**
     * Método que sirve para actualizar el grupo
     * @param groupUID UID del grupo a actualizar
     * @param onCompleteListener Función que se llamará al terminar la tarea de actualizar
     * el grupo dentro de la base de datos
     * */
    fun updateGroup(groupUID: String, onCompleteListener: (task: Task<Void>) -> Unit) {
        val membersToDelete = mutableListOf<String>()
        val friendsToInvite = mutableListOf<String>()

        for (member in _members.value) {
            if (!selectedUsers.any { selectedUser -> selectedUser.uid == member.uid }) {
                membersToDelete.add(member.uid)
            }
        }
        for (user in selectedUsers) {
            if (!_members.value.any { member -> member.uid == user.uid }) {
                friendsToInvite.add(user.uid)
            }
        }

        Log.d("prueba", "selected users $selectedUsers")
        Log.d("prueba", "membersToDelete $membersToDelete")
        Log.d("prueba", "friendsToInvite $friendsToInvite")

        val group = Group(
            "${Utilities.PROFILE_PIC_ST}images/$groupUID",
            currentUserUid,
            _groupName.value,
            _groupDescription.value,
            _startDate.value.toString(),
            _endDate.value.toString(),
            null,
            category = _groupCategory
        )
        repo.updateGroup(group, groupUID, membersToDelete, friendsToInvite)
            .addOnCompleteListener { task ->
                groupPhoto.let { pic ->
                    when (pic) {
                        is Uri -> {
                            storageRepository.saveImageFromUri(pic, groupUID)
                                .addOnCompleteListener {
                                    onCompleteListener(task)
                                }
                        }

                        is Bitmap -> {
                            storageRepository.saveImageFromBitmap(pic, groupUID)
                                .addOnCompleteListener {
                                    onCompleteListener(task)
                                }
                        }

                        else -> {
                            storageRepository.deletePhoto("images/$groupUID")
                                .addOnCompleteListener {
                                    onCompleteListener(task)
                                }
                        }
                    }
                }
            }
    }

    /**
     * Método que sirve para borrar un grupo
     * @param groupUID UID del grupo a borrar de la base datos
     * @param completeListener Función que se llama al terminar de eliminar el grupo
     * de la base de datos
     * */
    fun deleteGroup(groupUID: String, completeListener: (task: Task<Void>) -> Unit) {
        addMember(currentUserUid, User(), null)
        repo.deleteGroup(groupUID, _members.value.toList()).addOnCompleteListener {
            storageRepository.deletePhoto("images/$groupUID")
            completeListener(it)
        }
    }

    /**
     * Método que se llamará al pulsar sobre el input para escoger la fecha de inicio del grupo en la vista
     * Este mostrará una ventana emergente desde donde el usuario podrá escoger una fecha
     * @param context Contexto usado para mostrar la ventana emergente
     * @param view Vista en donde se mostrará la ventana emergente
     * */
    fun onStartDateClick(context: Context, view: View) {
        val dateResult = DateResult(
            context.getString(R.string.start_date_text),
            {
                val datePicker = it.findViewById<DatePicker>(R.id.datePicker)
                val response: String? = validateStartDate(getDate(datePicker), context)
                if (response != null) {
                    showToast(response, context)
                }
            }, {}
        )
        showDatePickerDialog(context, dateResult, view)
    }

    /**
     * Método que sirve para actualizar la lista de miembros cargados del grupo
     * @param newMembers Lista con los nuevos miembros del grupo cargado
     * */
    private fun updateList(newMembers: List<ListItemUiModel.User>) {
        _members.value = newMembers
    }

    /**
     * Método que sirve para añadir un miembro dentro de la lista de miembros del grupo cargado
     * @param uid UID del miembro a añadir
     * @param member Miembro a añadir
     * @param role Role que ocupa el miembro dentro del grupo
     * */
    private fun addMember(uid: String, member: User, role: ROLE?) {
        val updatedList = _members.value.toMutableList().apply {
            add(ListItemUiModel.User(uid, member, true, role))
        }
        updateList(updatedList)
    }

    /**
     * Método que sirve para eliminar un miembro de la lista de miembros del grupo cargado
     * @param memberUID UID del miembro a eliminar
     * */
    private fun removeMember(memberUID: String) {
        val updatedList = _members.value.toMutableList().apply {
            removeIf { u -> u.uid == memberUID }
        }
        selectedUsers.removeIf { u -> u.uid == memberUID }
        Log.d("integridad", "selectedUsers: $selectedUsers")
        updateList(updatedList)
    }

    /**
     * Objeto anónimo que implementa la interfaz [ChildEventListener] usado para estar
     * pendiente de los miembros del grupo actual
     * */
    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val key = snapshot.key
            val role = snapshot.getValue(ROLE::class.java)
            Log.d("prueba", "role: $role")
            key?.let {
                userRepo.findUserByUIDNotSuspend(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result.getValue(User::class.java)
                        user?.let { foundUser ->
                            if (currentUserUid != it) {
                                selectedUsers.add(ListItemUiModel.User(key, foundUser, true, role))
                                addMember(key, foundUser, role)
                                // se carga el rol de usuario actual para ver si puede o no realizar modificaciones
                            } else {
                                if (role != null) {
                                    _currentUserRole.value = role
                                }
                            }
                        }
                    } else {
                        Log.d("prueba", "Error cargando usuario ${task.exception?.message}")
                    }
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val key = snapshot.key
            val role = snapshot.getValue(ROLE::class.java)
            val updatedList = _members.value.toMutableList()
            try {
                updatedList.first { member -> member.uid == key }.role = role
            } catch (e: NoSuchElementException) {
                if (key == currentUserUid) {
                    role?.let { _currentUserRole.value = it }
                }
                Log.d("prueba", "NoSuchElementException NewGroupViewModel OnChildChanged")
            }
            updateList(emptyList())
            updateList(updatedList)
            Log.d("prueba", "updated list $updatedList")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            snapshot.key?.let {
                if (it == currentUserUid) {
                    onCurrentUserBanned?.let { it() }
                }
                removeMember(it)
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("prueba", "OnGroupChaged, previousChildName: $previousChildName")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("prueba", "OnGroupChaged, previousChildName: $error")
        }

    }

    val allGood: Boolean
        get() {
            return groupNameError.value.equals("") &&
                    groupDescriptionError.value.equals("") &&
                    startDate.value != null && _endDate.value != null
        }

    /**
     * Método que sirve para crear un nuevo grupo
     * @param currentUserUid UID del usuario que crea el grupo
     * @param username Nombre de usuario del usuario que crea el grupo
     * @param onCompleteListener Función que se llama al terminar la tarea de crear el grupo
     * */
    fun createNewGroup(
        currentUserUid: String,
        username: String,
        onCompleteListener: (task: Task<Void>) -> Unit
    ) {
        val members: MutableList<String> = selectedUsers.map { user -> user.uid }.toMutableList()
        Log.d("integridad", "miembros a invitar: $members")
        val group = Group(
            "${Utilities.PROFILE_PIC_ST}images/",
            currentUserUid,
            _groupName.value,
            _groupDescription.value,
            _startDate.value.toString(),
            _endDate.value.toString(),
            hashMapOf(currentUserUid to ROLE.ADMIN),
            category = _groupCategory
        )

        repo.createNewGroup(group, currentUserUid, members, username) { task, uid ->
            groupPhoto.let { pic ->
                when (pic) {
                    is Uri -> {
                        storageRepository.saveImageFromUri(pic, uid).addOnCompleteListener {
                            onCompleteListener(task)
                        }
                    }

                    is Bitmap -> {
                        storageRepository.saveImageFromBitmap(pic, uid).addOnCompleteListener {
                            onCompleteListener(task)
                        }
                    }

                    else -> {
                        onCompleteListener(task)
                    }
                }
            }
        }
    }

    /**
     * Método que sirve para obtener un objeto anónimo que implementa la interfaz [SearchView.OnQueryTextListener]
     * usado para filtrar los datos mostrados en el adapter de miembros
     * @param adapter Adaptador sobre el cual se realiza la filtración
     * */
    fun getSearchViewFilter(adapter: NewGroupFriendsAdapter): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    adapter.filterData(query)
                } else {
                    adapter.resetData()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filterData(newText)
                } else {
                    adapter.resetData()
                }
                return true
            }
        }
    }

    /**
     * Método que sirve para cargar los miembros del grupo seleccionado
     * @param groupUID UID del grupo del cual se desea cargar los miembros
     * */
    fun loadMembers(groupUID: String) {
        if (childEventsAdded) return
        repo.setGroupMembersChildEvents(groupUID, childEventListener)
        childEventsAdded = true
    }

    fun getSelectedList(): MutableList<ListItemUiModel.User> {
        return selectedUsers
    }

    /**
     * Método que sirve para mostrar una ventana emergente con un DatePicker para que el usuario
     * seleccione una fecha
     * @param context Contexto usado para mostrar la ventana emergente
     * @param result Objeto que contiene los datos usados para pintar la ventana emergente
     * @param view Vista sobre la cual se muestra la ventana emergente
     * */
    private fun showDatePickerDialog(context: Context, result: DateResult, view: View) {
        val dialogFactory = AlertDialogFactory(context)
        dialogFactory.createDatePickerDialog(view, result)
    }

    /**
     * Método que sirve para validar que la fecha de fin cumple el límite
     * @param date Fecha que se valida
     * @param context Contexto usado para acceder a los recursos de los aplicaciones y obtener
     * los mensajes de errores
     * @return El resultado de la validacion, nulo si es correcto
     * */
    private fun validateLimitEndDate(date: LocalDateTime, context: Context): String? {
        return if (date.isAfter(endDateLimit)) {
            context.getString(R.string.end_date_limit_error)
        } else {
            _endDate.postValue(date)
            null
        }
    }

    /**
     * Método que sirve para validar la fecha de fin
     * @param date Fecha que se valida
     * @param context Contexto usado para acceder a los recursos de los aplicaciones y obtener
     * los mensajes de errores
     * @return El resultado de la validacion, nulo si es correcto
     * */
    private fun validateEndDate(date: LocalDateTime, context: Context): String? {
        return if (_startDate.value != null) {
            if (date.isBefore(_startDate.value)) {
                context.getString(R.string.end_date_error)
            } else {
                validateLimitEndDate(date, context)
            }
        } else {
            validateLimitEndDate(date, context)
        }
    }

    /**
     * Método que sirve para validar que la fecha de inicio cumple el límite
     * @param date Fecha que se valida
     * @param context Contexto usado para acceder a los recursos de los aplicaciones y obtener
     * los mensajes de errores
     * @return El resultado de la validacion, nulo si es correcto
     * */
    private fun validateStartDateLimit(date: LocalDateTime, context: Context): String? {
        return if (date.isBefore(startDateLimit)) {
            context.getString(R.string.start_date_limit_error)
        } else {
            _startDate.postValue(date)
            null
        }
    }

    /**
     * Método que sirve para validar la fecha de inicio
     * @param date Fecha que se valida
     * @param context Contexto usado para acceder a los recursos de los aplicaciones y obtener
     * los mensajes de errores
     * @return El resultado de la validacion, nulo si es correcto
     * */
    private fun validateStartDate(date: LocalDateTime, context: Context): String? {
        return if (_endDate.value != null) {
            if (date.isAfter(_endDate.value)) {
                context.getString(R.string.end_date_error)
            } else {
                validateStartDateLimit(date, context)
            }
        } else {
            validateStartDateLimit(date, context)
        }
    }

    fun setGroupDescription(groupDescription: String) {
        _groupDescription.postValue(groupDescription)
    }

    /**
     * Método que sirve para validar la descripción del grupo
     * @param groupDescription Descripción del grupo a validar
     * @param context Contexto usado para acceder a los mensajes localizados de errores
     * */
    fun validateGroupDescription(groupDescription: String, context: Context) {
        val validator = GroupDescriptionValidator(context)
        _groupDescriptionError.postValue(validator.validate(groupDescription) ?: "")
    }

    fun setGroupName(groupName: String) {
        _groupName.postValue(groupName)
    }

    /**
     * Método que sirve para validar el nombre del grupo
     * @param groupName Nombre del grupo a validar
     * @param context Contexto usado para acceder a los mensajes localizados de errores
     * */
    fun validateGroupName(groupName: String, context: Context) {
        val validator = GroupNameValidator(context)
        _groupNameError.postValue(validator.validate(groupName) ?: "")
    }

    fun setStartDate(date: LocalDateTime) {
        _startDate.postValue(date)
    }

    fun setEndDate(date: LocalDateTime) {
        _endDate.postValue(date)
    }

    /**
     * Método que sirve para mostrar un [Toast] con mensaje error al cargar la foto
     * @param context Contexto usado para obtener el mensaje de error
     * */
    fun onPhotoLoadFail(context: Context) {
        Toast.makeText(context, context.getString(R.string.select_photo_error), Toast.LENGTH_SHORT)
            .show()
    }

    /**
     * Método que sirve para establecer la foto de grupo cargada desde la cámara
     * @param img Foto a cargar
     * @param context Contexto usado por Glide para cargar la foto en la vista
     * @param view Vista en donde se carga la foto
     * */
    fun onSuccessCamera(img: Bitmap, context: Context, view: ImageView) {
        setGroupPhoto(img)
        Glide.with(context).load(img).placeholder(R.drawable.default_group_pic).into(view)
    }

    /**
     * Método que sirve para establecer la foto de grupo cargada desde la galería
     * @param uri Uri de la foto a cargar
     * @param context Contexto usado por Glide para cargar la foto en la vista
     * @param view Vista en donde se carga la foto
     * */
    fun onSuccessGallery(uri: Uri, context: Context, view: ImageView) {
        setGroupPhoto(uri)
        Glide.with(context).load(uri).placeholder(R.drawable.default_group_pic).into(view)
    }

    /**
     * Método usado para eliminar la foto de grupo de la variable y la vista
     * @param context Contexto usado para cargar la foto de grupo por defecto en la vista
     * @param view Vista en donde poner la foto por defecto
     * */
    fun onDeletePhoto(context: Context, view: ImageView) {
        setGroupPhoto(null)
        Glide.with(context).load(R.drawable.default_group_pic).into(view)
    }

    /**
     * Método que sirve para cambiar el rol de un miembro del grupo
     * @param groupUID UID del grupo en donde cambiar el rol del miembro
     * @param userUID UID del miembro al cual se le cambiará el rol
     * @param newRole Rol a asignar al miembro
     * @param context Contexto usado para mostrar [Toast] indicando el resultado del cambio
     * */
    fun changeMemberRole(groupUID: String, userUID: String, newRole: ROLE, context: Context) {
        val isMember = _members.value.any { member -> member.uid == userUID }
        if (isMember) {
            repo.changeMemberRole(groupUID, userUID, newRole) {
                if (it.isSuccessful) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.change_role_success),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.change_role_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Método que sirve para eliminar de la lista de usuarios seleccionados
     * cualquier usuario presente en la lista pasada por argumento
     * @param users Lista de usuarios a eliminar
     * */
    fun cleanSelectedList(users: List<ListItemUiModel>) {
        selectedUsers.removeIf { selectedUser ->
            !users.any { u ->
                require(u is ListItemUiModel.User)
                u.uid == selectedUser.uid
            }
        }
    }

    fun setOnCurrentUserBanned(onCurrentUserBanned: () -> Unit) {
        this.onCurrentUserBanned = onCurrentUserBanned
    }
}