package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.Group
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.util.Utilities
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GroupRepository {
    private val usersRef: String = "users"
    private val groupsRef: String = "groups"
    private val database: DatabaseReference = Firebase.database.reference

    fun createNewGroup(group: Group, currentUserUid: String, onComplete:(task:Task<Void>, uid:String)->Unit){
        val key = database.child(groupsRef).push().key
        if (key == null) {
            Log.w("prueba", "Couldn't get push key for posts")
            return
        }
        group.pic += key
        val childUpdates = hashMapOf<String, Any>(
            "$groupsRef/$key" to group,
            "$usersRef/$currentUserUid/$groupsRef/$key" to true
        )
        database.updateChildren(childUpdates).addOnCompleteListener {
            onComplete(it, key)
        }
    }

    fun setGroupPic(path:String, uid:String){
        database.child(groupsRef).child(uid).child("pic").setValue(path)
    }

    fun updateGroup(group: Group, groupUID: String): Task<Void> {
        val childUpdates = hashMapOf<String, Any>(
            "$groupsRef/$groupUID" to group
        )
        return database.updateChildren(childUpdates)
    }

    fun deleteGroup(groupUID: String, members: List<ListItemUiModel.User>): Task<Void> {
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUID" to null
        )
        for (member in members) {
            childUpdates["$usersRef/${member.uid}/$groupsRef/$groupUID"] = null
        }
        return database.updateChildren(childUpdates)
    }

    fun setGroupChildEvents(currentUserUid: String, childEventListener: ChildEventListener) {
        database.child(usersRef).child(currentUserUid).child(groupsRef)
            .addChildEventListener(childEventListener)
    }

    fun setGroupMembersChildEvents(groupUID: String, childEventListener: ChildEventListener) {
        database.child(groupsRef).child(groupUID).child("members")
            .addChildEventListener(childEventListener)
    }

    fun findGroupByUID(groupUID: String): Task<DataSnapshot> {
        return database.child(groupsRef).child(groupUID).get()
    }
}