package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.Group
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GroupRepository {
    private val usersRef:String = "users"
    private val groupsRef: String = "groups"
    private val database:DatabaseReference = Firebase.database.reference

    fun createNewGroup(group: Group, currentUserUid: String):Task<Void>?{
        val key = database.child(groupsRef).push().key
        if(key == null){
            Log.w("prueba", "Couldn't get push key for posts")
            return null
        }
        val childUpdates = hashMapOf<String, Any>(
            "$groupsRef/$key" to group,
            "$usersRef/$currentUserUid/$groupsRef/$key" to true
        )
        return database.updateChildren(childUpdates)
    }
}