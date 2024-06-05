package com.example.budgetbuddy.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.icu.util.CurrencyAmount
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.Balance
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.ROLE
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.ExpenseRepository
import com.example.budgetbuddy.repositories.GroupRepository
import com.example.budgetbuddy.util.AlertDialogFactory
import com.example.budgetbuddy.util.DateResult
import com.example.budgetbuddy.util.Utilities
import com.example.budgetbuddy.validations.validators.GroupNameValidator
import com.example.budgetbuddy.validations.validators.NewExpenseTitleValidator
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NewExpenseViewModel: ViewModel() {
    private val repo = ExpenseRepository()
    private val groupRepo = GroupRepository()
    val expenseAdded: LiveData<Boolean> get() = _expenseAdded
    private val _expenseAdded = MutableLiveData<Boolean>()
    private val _newExpenseTitle: MutableLiveData<String> = MutableLiveData<String>()
    val newExpenseTitle: LiveData<String> = _newExpenseTitle
    private val _newExpenseTitleError = MutableLiveData<String?>()
    val newExpenseTitleError: LiveData<String?> = _newExpenseTitleError
    private val _newExpenseAmount = MutableLiveData<Double>()
    val newExpenseAmount: LiveData<Double> = _newExpenseAmount
    private val _debt = MutableLiveData<Double>()
    val debt: LiveData<Double> = _debt
    private val selectedUsers: MutableList<ListItemUiModel.User> = mutableListOf()
    private val endDateLimit = LocalDateTime.of(2030, 1, 1, 0, 0)
    private val startDateLimit = LocalDateTime.of(2000, 1, 1, 0, 0)
    private val _startDate = MutableLiveData<LocalDateTime?>()
    val startDate: LiveData<LocalDateTime?> = _startDate
    private val _endDate = MutableLiveData<LocalDateTime?>()
    val endDate: LiveData<LocalDateTime?> = _endDate
    private val _members: MutableStateFlow<List<ListItemUiModel.User>> =
        MutableStateFlow(emptyList())
    val members: StateFlow<List<ListItemUiModel.User>> = _members

    private val _currentGroupMembers = MutableLiveData<List<User>>()
    private val _selectedUsers = MutableLiveData<List<User>>()
    val currentGroupMembers: LiveData<List<User>> get() = _currentGroupMembers
    private val _payer: MutableLiveData<User> = MutableLiveData<User>()
    val payerUserName: LiveData<User> get() = _payerUserName
    private val _payerUserName: MutableLiveData<User> = MutableLiveData<User>()
    val payer: LiveData<User> get() = _payer



    fun onStartDateClick(context: Context, view: View) {
        val dateResult = DateResult(
            context.getString(R.string.new_expense),
            {
                val datePicker = it.findViewById<DatePicker>(R.id.datePicker)
                val response: String? = validateStartDateLimit(getDate(datePicker), context)
                val response1: String? = validateLimitEndDate(getDate(datePicker), context)
                if (response != null) {
                    showToast(response, context)
                }else if(response1!= null){
                    showToast(response1, context)
                }
            }, {}
        )
        showDatePickerDialog(context, dateResult, view)
       // showDatePickerDialog(context, dateResult, view)
    }

    private fun showDatePickerDialog(context: Context, result: DateResult, view: View) {
        val dialogFactory = AlertDialogFactory(context)
        dialogFactory.createDatePickerDialog(view, result)
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

    fun setNewExpenseTitle(newExpenseTitle: String) {
        _newExpenseTitle.postValue(newExpenseTitle)
    }

    fun setNewExpenseAmount(newExpenseAmount: Double) {
        _newExpenseAmount.postValue(newExpenseAmount)
    }

    fun validateNewExpenseTitle(newExpenseTitle: String, context: Context) {
        val validator = NewExpenseTitleValidator(context)
        _newExpenseTitleError.postValue(validator.validate(newExpenseTitle) ?: "")
    }

    // Validaciones fechas
    private fun validateStartDateLimit(date: LocalDateTime, context: Context): String? {
        return if (date.isBefore(startDateLimit)) {
            context.getString(R.string.start_date_limit_error)
        } else {
            _startDate.postValue(date)
            null
        }
    }
    private fun validateLimitEndDate(date: LocalDateTime, context: Context): String? {
        return if (date.isAfter(endDateLimit)) {
            context.getString(R.string.end_date_limit_error)
        } else {
            _endDate.postValue(date)
            null
        }
    }
    val allGood: Boolean
        get() {
            return newExpenseTitleError.value.equals("") &&
                    startDate.value != null && _endDate.value != null
        }
    fun createNewExpense(
        currentUserUid: String,
        currentUserName: String,
        currentGroupId: String,
        onCompleteListener: (task: Task<Void>) -> Unit
    ) {
        val expense = Expense(
            _newExpenseTitle.value,
            _newExpenseAmount.value,
            _startDate.value.toString(),
            payer = currentUserUid,
            _debt.value,
            payerUserName = currentUserName

        )



        repo.createNewExpense(expense, currentGroupId) { task, uid ->
            if (task.isSuccessful) {

            } else {
                Log.e(TAG, "Error creating new expense", task.exception)
            }

    }
    }
}
