package com.example.budgetbuddy.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.repositories.ExpenseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel


class ExpenseViewModel : ViewModel() {
    private val expenseRepository = ExpenseRepository()


    private val _expenses = MutableLiveData<List<Expense>>()
    val expenses: LiveData<List<Expense>> = _expenses

    fun loadExpenses(groupId: String) {
        expenseRepository.getExpenses(groupId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val expensesList = mutableListOf<Expense>()
                snapshot.children.forEach { expenseSnapshot ->
                    val expense = expenseSnapshot.getValue(Expense::class.java)
                    expense?.let { expensesList.add(it) }
                }
                _expenses.value = expensesList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ExpenseViewModel", "Database error: ${error.message}")
            }
        })
    }

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
}