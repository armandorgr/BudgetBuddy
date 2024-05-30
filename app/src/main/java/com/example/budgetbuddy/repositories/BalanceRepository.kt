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

class BalanceRepository {
    private val database: DatabaseReference = Firebase.database.reference
    private val groupsRef: String = "groups"
    private val balancesRef: String = "balances"


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

    fun findBalance(currentGroupId: String, user1: String, user2: String): Task<DataSnapshot> {
        val users = user1 + user2
        return database.child(groupsRef).child(currentGroupId).child(balancesRef).child(users).get()
    }

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