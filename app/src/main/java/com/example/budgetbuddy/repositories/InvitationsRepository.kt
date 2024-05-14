package com.example.budgetbuddy.repositories

import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.model.ROLE
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InvitationsRepository {
    private val usersRef: String = "users"
    private val groupsRef: String = "groups"
    private val database: DatabaseReference = Firebase.database.reference

    fun writeNewInvitation(
        uid: String,
        fromUid: String,
        invitation: InvitationUiModel
    ) {
        database.child(usersRef).child(uid).child("invitations").child(fromUid).setValue(invitation)
    }

    fun sendFriendsRequest(
        uid: String,
        fromUid: String,
        invitation: InvitationUiModel,
        onComplete:(task: Task<Void>)->Unit
    ) {
        database.child(usersRef).child(uid).child("invitations").child(fromUid).setValue(invitation).addOnCompleteListener(onComplete)
    }


    fun getInvitationsReference(uid: String): DatabaseReference {
        return database.child(usersRef).child(uid).child("invitations")
    }

    fun deleteInvitation(userUid: String, senderUid: String): Task<Void> {
        return database.child(usersRef).child(userUid).child("invitations").child(senderUid).removeValue()
    }

    fun confirmFriendRequestInvitation(currentUserUid: String, invitationSenderUid: String ){
        val childUpdate = hashMapOf<String, Any?>(
            "$currentUserUid/friends/$invitationSenderUid" to true,
            "$invitationSenderUid/friends/$currentUserUid" to true,
            "$currentUserUid/invitations/$invitationSenderUid" to null
        )
        database.child(usersRef).updateChildren(childUpdate)
    }

    fun confirmGroupInvitation(currentUserUid: String, invitationSenderUid: String){
        val childUpdate = hashMapOf<String, Any?>(
            "$usersRef/$currentUserUid/groups/$invitationSenderUid" to true,
            "$groupsRef/$invitationSenderUid/members/$currentUserUid" to ROLE.MEMBER,
            "$usersRef/$currentUserUid/invitations/$invitationSenderUid" to null
        )
        database.updateChildren(childUpdate)
    }
}