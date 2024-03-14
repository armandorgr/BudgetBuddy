package com.example.budgetbuddy.repositories

import com.example.budgetbuddy.model.InvitationUiModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InvitationsRepository {
    private val ref: String = "users"
    private val database: DatabaseReference = Firebase.database.getReference(ref)

    fun writeNewInvitation(
        uid: String,
        fromUid: String,
        invitation: InvitationUiModel
    ) {
        database.child(uid).child("invitations").child(fromUid).setValue(invitation)
    }

    fun getInvitationsReference(uid: String): DatabaseReference {
        return database.child(uid).child("invitations")
    }

    fun deleteInvitation(userUid: String, senderUid: String): Task<Void> {
        return database.child(userUid).child("invitations").child(senderUid).removeValue()
    }

    fun confirmFriendRequestInvitation(currentUserUid: String, invitationSenderUid: String ){
        val childUpdate = hashMapOf<String, Any?>(
            "$currentUserUid/friends/$invitationSenderUid" to true,
            "$invitationSenderUid/friends/$currentUserUid" to true,
            "$currentUserUid/invitations/$invitationSenderUid" to null
        )
        database.updateChildren(childUpdate)
    }
}