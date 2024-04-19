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
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import javax.inject.Inject


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


    fun leaveGroup(groupUID: String, onCompleteListener: (task: Task<Void>) -> Unit) {
        repo.leaveGroup(currentUserUid, groupUID, onCompleteListener)
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

    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

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
            null
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

    fun deleteGroup(groupUID: String, completeListener: (task: Task<Void>) -> Unit) {
        addMember(currentUserUid, User(), null)
        repo.deleteGroup(groupUID, _members.value.toList()).addOnCompleteListener {
            storageRepository.deletePhoto("images/$groupUID")
            completeListener(it)
        }
    }

    fun onStartDateClick(context: Context, view: View) {
        val dateResult = DateResult(
            context.getString(R.string.new_group),
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

    fun resetFields() {
        _startDate.postValue(null)
        _endDate.postValue(null)
        _groupName.postValue("")
        _groupDescription.postValue("")
        _groupNameError.postValue(null)
        _groupDescriptionError.postValue(null)
        Log.d("prueba", "selected users $selectedUsers")
        selectedUsers.clear()
    }

    private fun updateList(newMembers: List<ListItemUiModel.User>) {
        _members.value = newMembers
    }

    private fun addMember(uid: String, member: User, role: ROLE?) {
        val updatedList = _members.value.toMutableList().apply {
            add(ListItemUiModel.User(uid, member, true, role))
        }
        updateList(updatedList)
    }

    private fun removeMember(memberUID: String) {
        val updatedList = _members.value.toMutableList().apply {
            removeIf { u -> u.uid == memberUID }
        }
        updateList(updatedList)
    }

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
            try{
                updatedList.first { member -> member.uid==key }.role = role
            }catch (e: NoSuchElementException){
                Log.d("prueba","NoSuchElementException NewGroupViewModel OnChildChanged")
            }
            updateList(emptyList())
            updateList(updatedList)
            Log.d("prueba","updated list $updatedList")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            snapshot.key?.let { removeMember(it) }
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

    fun createNewGroup(
        currentUserUid: String,
        username: String,
        onCompleteListener: (task: Task<Void>) -> Unit
    ) {
        val members: MutableList<String> = selectedUsers.map { user -> user.uid }.toMutableList()
        val group = Group(
            "${Utilities.PROFILE_PIC_ST}images/",
            currentUserUid,
            _groupName.value,
            _groupDescription.value,
            _startDate.value.toString(),
            _endDate.value.toString(),
            hashMapOf(currentUserUid to ROLE.ADMIN)
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

    fun loadMembers(groupUID: String) {
        if (childEventsAdded) return
        repo.setGroupMembersChildEvents(groupUID, childEventListener)
        childEventsAdded = true
    }

    fun getSelectedList(): MutableList<ListItemUiModel.User> {
        return selectedUsers
    }

    fun showDatePickerDialog(context: Context, result: DateResult, view: View) {
        val dialogFactory = AlertDialogFactory(context)
        dialogFactory.createDatePickerDialog(view, result)
    }

    private fun validateLimitEndDate(date: LocalDateTime, context: Context): String? {
        return if (date.isAfter(endDateLimit)) {
            context.getString(R.string.end_date_limit_error)
        } else {
            _endDate.postValue(date)
            null
        }
    }

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

    private fun validateStartDateLimit(date: LocalDateTime, context: Context): String? {
        return if (date.isBefore(startDateLimit)) {
            context.getString(R.string.start_date_limit_error)
        } else {
            _startDate.postValue(date)
            null
        }
    }

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

    fun validateGroupDescription(groupDescription: String, context: Context) {
        val validator = GroupDescriptionValidator(context)
        _groupDescriptionError.postValue(validator.validate(groupDescription) ?: "")
    }

    fun setGroupName(groupName: String) {
        _groupName.postValue(groupName)
    }

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

    fun onPhotoLoadFail(context: Context) {
        Toast.makeText(context, context.getString(R.string.select_photo_error), Toast.LENGTH_SHORT)
            .show()
    }

    fun onSuccessCamera(img: Bitmap, context: Context, view: ImageView) {
        setGroupPhoto(img)
        Glide.with(context).load(img).placeholder(R.drawable.default_group_pic).into(view)
    }

    fun onSuccessGallery(uri: Uri, context: Context, view: ImageView) {
        setGroupPhoto(uri)
        Glide.with(context).load(uri).placeholder(R.drawable.default_group_pic).into(view)
    }

    fun onDeletePhoto(context: Context, view: ImageView) {
        setGroupPhoto(null)
        Glide.with(context).load(R.drawable.default_group_pic).into(view)
    }

    fun changeMemberRole(groupUID: String, userUID: String, newRole: ROLE, context: Context) {
        val isMember = _members.value.any { member -> member.uid == userUID }
        if(isMember){
            repo.changeMemberRole(groupUID, userUID, newRole){
                if(it.isSuccessful){
                    Toast.makeText(context, context.getString(R.string.change_username_success), Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, context.getString(R.string.change_username_fail), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}