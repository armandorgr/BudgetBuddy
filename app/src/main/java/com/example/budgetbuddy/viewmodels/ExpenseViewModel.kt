package com.example.budgetbuddy.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.repositories.ExpenseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * ViewModel para manejar las operaciones relacionadas con los gastos en la aplicación.
 * Se encarga de cargar los gastos de un grupo específico y actualizar un gasto existente.
 *
 * @author Álvaro Aparicio
 */
class ExpenseViewModel : ViewModel() {

    // Repositorio para acceder a los datos relacionados con los gastos.
    private val expenseRepository = ExpenseRepository()

    // LiveData para contener la lista de gastos.
    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> = _expenses


    /**
     * Método para cargar los gastos de un grupo específico.
     * @param groupId El ID del grupo del que se cargarán los gastos.
     */
    fun loadExpenses(groupId: String) {
        expenseRepository.getExpenses(groupId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val expensesList = mutableListOf<Expense>()
                snapshot.children.forEach { expenseSnapshot ->
                  var expenseUID = expenseSnapshot.key
                    val expense = expenseSnapshot.getValue(Expense::class.java)
                    expense?.let { expensesList.add(it)
                    it.expenseUID = expenseUID
                    }
                }
                _expenses.value = expensesList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ExpenseViewModel", "Database error: ${error.message}")
            }
        })
    }

    /**
     * Método para actualizar un gasto existente.
     * @param expenseId El ID del gasto que se actualizará.
     * @param updatedExpense El objeto Expense actualizado.
     */
    fun updateExpense(expenseId: String, updatedExpense: Expense) {
        expenseRepository.updateExpense(expenseId, updatedExpense) { task ->
            if (task.isSuccessful) {
                // La actualización se completó con éxito
            } else {
                // La actualización no se completó
                Log.e(TAG, "Error updating expense", task.exception)
            }
        }
    }

    /**
     * Método que sirve para borrar un gasto
     * @param groupUID del grupo donde se encuentra el gasto
     * @param expenseUID del gasto a borrar de la base datos
     * @param completeListener Función que se llama al terminar de eliminar el gasto
     * de la base de datos
     * */
    fun deleteExpense(groupUID: String, expenseUID: String, completeListener: (task: Task<Void>) -> Unit) {
        expenseRepository.deleteExpense(groupUID, expenseUID).addOnCompleteListener {
            completeListener(it)
        }
    }
}