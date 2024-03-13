package com.example.budgetbuddy.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.R
import com.example.budgetbuddy.adapters.recyclerView.NewGroupFriendsAdapter
import com.example.budgetbuddy.model.DateSent
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.GroupRepository
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.DateResult
import com.example.budgetbuddy.util.Result
import com.example.budgetbuddy.validations.validators.GroupDescriptionValidator
import com.example.budgetbuddy.validations.validators.GroupNameValidator
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class NewGroupViewModel @Inject constructor(
    private val repo: GroupRepository
) : ViewModel() {
    private val startDateLimit = LocalDateTime.of(2000, 1, 1, 0, 0)
    private val endDateLimit = LocalDateTime.of(2030, 1, 1, 0, 0)
    private val selectedUsers: MutableList<User> = mutableListOf()
    private val _startDate = MutableLiveData<LocalDateTime?>()
    val startDate: LiveData<LocalDateTime?> = _startDate

    fun resetFields(){
        _startDate.postValue(null)
        _endDate.postValue(null)
        _groupName.postValue("")
        _groupDescription.postValue("")
        _groupNameError.postValue(null)
        _groupDescriptionError.postValue(null)
        Log.d("prueba", "selected users $selectedUsers")
        selectedUsers.clear()
    }

    val allGood: Boolean
        get() {
            return groupNameError.value.equals("") &&
                    groupDescriptionError.value.equals("") &&
                    selectedUsers.size >= 1 &&
                    startDate.value != null && _endDate.value != null
        }

    private val _endDate = MutableLiveData<LocalDateTime?>()
    val endDate: LiveData<LocalDateTime?> = _endDate

    private val _groupName = MutableLiveData<String>()
    val groupName: LiveData<String> = _groupName

    private val _groupDescription = MutableLiveData<String>()
    val groupDescription: LiveData<String> = _groupDescription

    private val _groupNameError = MutableLiveData<String?>()
    val groupNameError: LiveData<String?> = _groupNameError

    private val _groupDescriptionError = MutableLiveData<String?>()
    val groupDescriptionError: LiveData<String?> = _groupDescriptionError

    fun createNewGroup(currentUserUid: String, onCompleteListener: (task: Task<Void>) -> Unit) {
        val members: HashMap<String, Boolean> =
            selectedUsers.map { it.uid }.associateWith { true } as HashMap<String, Boolean>
        members[currentUserUid] = true
        val group = Group(
            currentUserUid,
            _groupName.value,
            _groupDescription.value,
            _startDate.value.toString(),
            _endDate.value.toString(),
            members
        )
        repo.createNewGroup(group, currentUserUid)?.addOnCompleteListener{
            onCompleteListener(it)
        }
    }
    fun getSelectedList(): MutableList<User> {
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

    fun validateEndDate(date: LocalDateTime, context: Context): String? {
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

    fun validateStartDate(date: LocalDateTime, context: Context): String? {
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
}