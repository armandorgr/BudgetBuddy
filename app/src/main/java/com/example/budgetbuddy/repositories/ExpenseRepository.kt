package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.Balance
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class ExpenseRepository {
    private val expensesRef: String = "expenses"
    private val groupsRef: String = "groups"
    private val database: DatabaseReference = Firebase.database.reference


    fun createNewExpense(expense: Expense, currentGroupId: String, onComplete:(task: Task<Void>, uid:String)->Unit){
        val key = database.child(expensesRef).push().key
        if (key == null) {
            Log.w("prueba", "Couldn't get push key for posts")
            return
        }
        Log.d( "prueba",expense.amount.toString())
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$currentGroupId/$expensesRef/$key/title" to expense.title.toString(),
            "$groupsRef/$currentGroupId/$expensesRef/$key/amount" to expense.amount,
            "$groupsRef/$currentGroupId/$expensesRef/$key/date" to expense.date.toString(),
            "$groupsRef/$currentGroupId/$expensesRef/$key/payer" to expense.payer,
            "$groupsRef/$currentGroupId/$expensesRef/$key/debt" to expense.debt,
            "$groupsRef/$currentGroupId/$expensesRef/$key/payerUserName" to expense.payerUserName,
        )
        database.updateChildren(childUpdates).addOnCompleteListener {
            onComplete(it, key)
        }

    }

    fun updateExpense(expenseId: String, updatedExpense: Expense, onComplete: (Task<Void>) -> Unit) {
        val expenseRef = database.child("expenses").child(expenseId)
        val expenseUpdates = hashMapOf<String, Any?>(
            "title" to updatedExpense.title,
            "amount" to updatedExpense.amount,
            "date" to updatedExpense.date,
            "payer" to updatedExpense.payer,
            "debt" to updatedExpense.debt,
            "payerUserName" to updatedExpense.payerUserName
        )
        expenseRef.updateChildren(expenseUpdates)
            .addOnCompleteListener { onComplete(it) }
    }
    fun getExpenses(groupId: String): DatabaseReference {
        return database.child("groups").child(groupId).child("/expenses")
    }

    companion object {
        private const val TAG = "ExpenseRepository"
    }



}