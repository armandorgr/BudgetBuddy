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

/**
 * ViewModel para manejar la creación de un nuevo gasto en la aplicación.
 * Se encarga de validar los datos del nuevo gasto y crearlo en la base de datos.
 *
 * @author Álvaro Aparicio
 */
class NewExpenseViewModel: ViewModel() {
    // Repositorios para acceder a los datos relacionados con los gastos y los grupos.
    private val repo = ExpenseRepository()
    private val groupRepo = GroupRepository()

    // LiveData para indicar si se ha añadido correctamente un nuevo gasto.
    val expenseAdded: LiveData<Boolean> get() = _expenseAdded
    private val _expenseAdded = MutableLiveData<Boolean>()

    // LiveData para contener el título del nuevo gasto.
    private val _newExpenseTitle: MutableLiveData<String> = MutableLiveData<String>()
    val newExpenseTitle: LiveData<String> = _newExpenseTitle

    // LiveData para contener el monto del nuevo gasto.
    private val _newExpenseTitleError = MutableLiveData<String?>()
    val newExpenseTitleError: LiveData<String?> = _newExpenseTitleError

    // LiveData para contener el monto del nuevo gasto.
    private val _newExpenseAmount = MutableLiveData<Double>()
    val newExpenseAmount: LiveData<Double> = _newExpenseAmount


    private val _debt = MutableLiveData<Double>()
    val debt: LiveData<Double> = _debt


    private val selectedUsers: MutableList<ListItemUiModel.User> = mutableListOf()

    // LiveData para contener la fecha de fin del nuevo gasto.
    private val endDateLimit = LocalDateTime.of(2030, 1, 1, 0, 0)

    // LiveData para contener la fecha de inicio del nuevo gasto.
    private val startDateLimit = LocalDateTime.of(2000, 1, 1, 0, 0)
    private val _startDate = MutableLiveData<LocalDateTime?>()
    val startDate: LiveData<LocalDateTime?> = _startDate
    private val _endDate = MutableLiveData<LocalDateTime?>()
    val endDate: LiveData<LocalDateTime?> = _endDate

    // MutableStateFlow para contener la lista de miembros del grupo.
    private val _members: MutableStateFlow<List<ListItemUiModel.User>> =
        MutableStateFlow(emptyList())
    val members: StateFlow<List<ListItemUiModel.User>> = _members

    // LiveData para contener los miembros actuales del grupo.
    private val _currentGroupMembers = MutableLiveData<List<User>>()
    private val _selectedUsers = MutableLiveData<List<User>>()

    // LiveData para contener los miembros actuales del grupo.
    val currentGroupMembers: LiveData<List<User>> get() = _currentGroupMembers

    // LiveData para contener el usuario que pagó el gasto.
    private val _payer: MutableLiveData<User> = MutableLiveData<User>()

    // LiveData para contener el nombre del usuario que pagó el gasto.
    val payerUserName: LiveData<User> get() = _payerUserName
    private val _payerUserName: MutableLiveData<User> = MutableLiveData<User>()
    val payer: LiveData<User> get() = _payer


    /**
     * Método para manejar el clic en la fecha de inicio.
     * @param context El contexto de la aplicación.
     * @param view La vista que recibió el clic.
     */
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

    /**
     * Método para mostrar el diálogo de selección de fecha.
     * @param context El contexto de la aplicación.
     * @param result El resultado del diálogo de fecha.
     * @param view La vista que recibió el clic.
     */
    private fun showDatePickerDialog(context: Context, result: DateResult, view: View) {
        val dialogFactory = AlertDialogFactory(context)
        dialogFactory.createDatePickerDialog(view, result)
    }

    /**
     * Método para obtener la fecha seleccionada del DatePicker.
     * @param datePicker El DatePicker que contiene la fecha seleccionada.
     * @return La fecha seleccionada como LocalDateTime.
     */
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
     * Método para mostrar un mensaje Toast.
     * @param message El mensaje que se mostrará.
     * @param context El contexto de la aplicación.
     */
    private fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Método para establecer el título del nuevo gasto.
     * @param newExpenseTitle El título del nuevo gasto.
     */
    fun setNewExpenseTitle(newExpenseTitle: String) {
        _newExpenseTitle.postValue(newExpenseTitle)
    }

    /**
     * Método para establecer el monto del nuevo gasto.
     * @param newExpenseAmount El monto del nuevo gasto.
     */
    fun setNewExpenseAmount(newExpenseAmount: Double) {
        _newExpenseAmount.postValue(newExpenseAmount)
    }

    /**
     * Método para validar el título del nuevo gasto.
     * @param newExpenseTitle El título del nuevo gasto a validar.
     * @param context El contexto de la aplicación.
     */
    fun validateNewExpenseTitle(newExpenseTitle: String, context: Context) {
        val validator = NewExpenseTitleValidator(context)
        _newExpenseTitleError.postValue(validator.validate(newExpenseTitle) ?: "")
    }

    /**
     * Método para validar la fecha de inicio del nuevo gasto.
     * @param date La fecha de inicio a validar.
     * @param context El contexto de la aplicación.
     * @return Mensaje de error si la validación falla, de lo contrario, null.
     */
    private fun validateStartDateLimit(date: LocalDateTime, context: Context): String? {
        return if (date.isBefore(startDateLimit)) {
            context.getString(R.string.start_date_limit_error)
        } else {
            _startDate.postValue(date)
            null
        }
    }

    /**
     * Método para validar la fecha de fin del nuevo gasto.
     * @param date La fecha de fin a validar.
     * @param context El contexto de la aplicación.
     * @return Mensaje de error si la validación falla, de lo contrario, null.
     */
    private fun validateLimitEndDate(date: LocalDateTime, context: Context): String? {
        return if (date.isAfter(endDateLimit)) {
            context.getString(R.string.end_date_limit_error)
        } else {
            _endDate.postValue(date)
            null
        }
    }

    /**
     * Propiedad que indica si todos los datos del nuevo gasto son válidos.
     */
    val allGood: Boolean
        get() {
            return newExpenseTitleError.value.equals("") &&
                    startDate.value != null && _endDate.value != null
        }

    /**
     * Método para crear un nuevo gasto.
     * @param currentUserUid El ID del usuario que crea el gasto.
     * @param currentUserName El nombre del usuario que crea el gasto.
     * @param currentGroupId El ID del grupo al que pertenece el gasto.
     * @param onCompleteListener La acción a realizar después de crear el gasto.
     */
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
