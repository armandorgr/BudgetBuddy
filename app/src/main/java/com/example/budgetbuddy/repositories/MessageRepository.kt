package com.example.budgetbuddy.repositories

import android.util.Log
import com.example.budgetbuddy.model.Message
import com.example.budgetbuddy.util.Utilities
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MessageRepository {
    private val groupsRef: String = "groups"
    private val messagesRef: String = "messages"
    private val database: DatabaseReference = Firebase.database.reference

    fun writeNewMessage(groupUID: String, message: Message) {
        val key = database.child(groupsRef).child(groupUID).child(messagesRef).push().key
        if (key == null) {
            Log.w("prueba", "Couldn't get push key for messages")
            return
        }
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUID/$messagesRef/$key/text" to message.text,
            "$groupsRef/$groupUID/$messagesRef/$key/senderUID" to message.senderUID,
            "$groupsRef/$groupUID/$messagesRef/$key/sentDate" to ServerValue.TIMESTAMP,
            "$groupsRef/$groupUID/$messagesRef/$key/type" to message.type,
            "$groupsRef/$groupUID/$messagesRef/$key/imgPath" to (Utilities.PROFILE_PIC_ST + key),
        )
        database.updateChildren(childUpdates)
    }

    fun getNewKey(groupUID: String): String? {
        return database.child(groupsRef).child(groupUID).child(messagesRef).push().key
    }

    fun writeNewImageMessage(groupUID: String, key:String, message: Message): Task<Void> {
        val childUpdates = hashMapOf<String, Any?>(
            "$groupsRef/$groupUID/$messagesRef/$key/text" to message.text,
            "$groupsRef/$groupUID/$messagesRef/$key/senderUID" to message.senderUID,
            "$groupsRef/$groupUID/$messagesRef/$key/sentDate" to ServerValue.TIMESTAMP,
            "$groupsRef/$groupUID/$messagesRef/$key/type" to message.type,
            "$groupsRef/$groupUID/$messagesRef/$key/imgPath" to (Utilities.PROFILE_PIC_ST + key),
        )
        return database.updateChildren(childUpdates)
    }
}