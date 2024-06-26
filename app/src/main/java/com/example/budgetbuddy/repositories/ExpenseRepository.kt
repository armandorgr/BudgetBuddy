package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.Balance
import com.example.budgetbuddy.model.Expense
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.log

/**
 * Clase repositorio para la gestión de gastos en la base de datos.
 *
 * Esta clase proporciona métodos para realizar operaciones CRUD relacionadas con los gastos
 * en la base de datos Firebase Realtime Database.
 *
 * La forma de trabajar con la base de datos fue consultada en la documentación de Firebase Realtime Database:
 *  https://firebase.google.com/docs/database/android/read-and-write?hl=es
 *  https://firebase.google.com/docs/database/android/lists-of-data?hl=es
 *
 * @property expensesRef Referencia a la colección de gastos en la base de datos.
 * @property groupsRef Referencia a la colección de grupos en la base de datos.
 * @property database Referencia a la base de datos Firebase.
 * @author Álvaro Aparicio
 */
class ExpenseRepository {
    private val expensesRef: String = "expenses"
    private val groupsRef: String = "groups"
    private val balancesRef: String = "balances"
    private val database: DatabaseReference = Firebase.database.reference

    /**
     * Crea un nuevo gasto en la base de datos.
     *
     * @param expense El gasto a crear.
     * @param currentGroupId El ID del grupo actual.
     * @param onComplete Callback que se llama cuando se completa la tarea.
     */
    fun createNewExpense(expense: Expense,members: List<String>, currentGroupId: String, onComplete:(task: Task<Void>, uid:String)->Unit){
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

        Log.d("prueba", "Members: $members")

        val amount = expense.amount
        for (i in members.indices) {

                val user1 = members[i]

                Log.d("users", "user1: $user1")
                Log.d("members", "$members")
                /*
                val balance = Balance(
                    user1 = user1,
                    user2 = user2,
                    amountUser1 = 0.0,
                    amountUser2 = 0.0
                )
                */

                //childUpdates.put("$groupsRef/$currentGroupId/$balancesRef/$user1/$user2",0);

                /*
                "$groupsRef/$key/$balancesRef/$user2/user2" to 0,
                "$groupsRef/$key/$balancesRef/$user1/amountUser1" to 0.0,
                "$groupsRef/$key/$balancesRef/$user2/amountUser2" to 0.0,*/

            }


    }

    /**
     * Actualiza un gasto en la base de datos.
     *
     * @param expenseId El ID del gasto a actualizar.
     * @param updatedExpense El gasto actualizado.
     * @param onComplete Callback que se llama cuando se completa la tarea.
     */
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

    /**
     * Obtiene una referencia a la colección de gastos de un grupo específico.
     *
     * @param groupId El ID del grupo.
     * @return La referencia a la colección de gastos.
     */
    fun getExpenses(groupId: String): DatabaseReference {
        return database.child("groups").child(groupId).child("/expenses")
    }

    companion object {
        private const val TAG = "ExpenseRepository"
    }

    /**
     * Método que sirve para eliminar un gasto de la base de datos
     * @param groupUID (El UID del grupo donde se encuentra ese gasto)
     * @param expenseUID (El UID del gasto en cuestión a borrar)
     * @return La tarea de eliminar el gasto.
     * */
    fun deleteExpense(groupUID: String, expenseUID: String): Task<Void> {
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUID/$expensesRef/$expenseUID" to null
        )

        return database.updateChildren(childUpdates)
    }
}