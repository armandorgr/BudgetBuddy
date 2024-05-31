package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.Balance
import com.example.budgetbuddy.model.Expense
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Clase repositorio para la gestión de balances en la base de datos.
 *
 * Esta clase proporciona métodos para realizar operaciones CRUD relacionadas con los balances
 * en la base de datos Firebase Realtime Database.
 *
 * La forma de trabajar con la base de datos fue consultada en la documentación de Firebase Realtime Database:
 *  https://firebase.google.com/docs/database/android/read-and-write?hl=es
 *  https://firebase.google.com/docs/database/android/lists-of-data?hl=es
 *
 * @property database Referencia a la base de datos Firebase.
 * @property groupsRef Referencia a la colección de grupos en la base de datos.
 * @property balancesRef Referencia a la colección de balances en la base de datos.
 * @constructor Crea un repositorio de balances.
 * @author Álvaro Aparicio
 */
class BalanceRepository {
    private val database: DatabaseReference = Firebase.database.reference
    private val groupsRef: String = "groups"
    private val balancesRef: String = "balances"

    /**
     * Crea un nuevo balance en la base de datos.
     *
     * @param balance El balance a crear.
     * @param currentGroupId El ID del grupo actual.
     * @param onComplete Callback que se llama cuando se completa la tarea.
     */
    fun createNewBalance(balance: Balance, currentGroupId: String, onComplete:(task: Task<Void>, uid:String)->Unit){
        val key = balance.user1 + balance.user2

        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$currentGroupId/$balancesRef/$key/user1" to balance.user1,
            "$groupsRef/$currentGroupId/$balancesRef/$key/user2" to balance.user2,
            "$groupsRef/$currentGroupId/$balancesRef/$key/amountUser1" to balance.amountUser1,
            "$groupsRef/$currentGroupId/$balancesRef/$key/amountUser2" to balance.amountUser2,
        )

        database.updateChildren(childUpdates).addOnCompleteListener {
            onComplete(it, key)
        }
    }

    /**
     * Busca un balance en la base de datos.
     *
     * @param currentGroupId El ID del grupo actual.
     * @param user1 El ID del primer usuario.
     * @param user2 El ID del segundo usuario.
     * @return Tarea que devuelve el resultado de la búsqueda.
     */
    fun findBalance(currentGroupId: String, user1: String, user2: String): Task<DataSnapshot> {
        val users = user1 + user2
        return database.child(groupsRef).child(currentGroupId).child(balancesRef).child(users).get()
    }

    /**
     * Actualiza un balance en la base de datos.
     *
     * @param balance El balance actualizado.
     * @param currentGroupId El ID del grupo actual.
     * @param onComplete Callback que se llama cuando se completa la tarea.
     */
    fun updateBalance(balance: Balance,currentGroupId: String ,onComplete:(task: Task<Void>, uid:String)->Unit){
        val key = balance.user1 + balance.user2
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$currentGroupId/$balancesRef/$key/user1" to balance.user1,
            "$groupsRef/$currentGroupId/$balancesRef/$key/user2" to balance.user2,
            "$groupsRef/$currentGroupId/$balancesRef/$key/amountUser1" to balance.amountUser1,
            "$groupsRef/$currentGroupId/$balancesRef/$key/amountUser2" to balance.amountUser2,
        )
        database.updateChildren(childUpdates).addOnCompleteListener {
            onComplete(it, key)
        }
    }
}