package com.example.budgetbuddy.repositories

import com.example.budgetbuddy.model.InvitationUiModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class InvitationsRepository {
    private val ref:String = "invitations"
    private val database:DatabaseReference = Firebase.database.getReference(ref)

    suspend fun writeNewInvitation(uid:String, invitation: InvitationUiModel):Boolean{
        return try{
            val task = database.child(uid).setValue(invitation)
            task.await()
            task.isSuccessful
        }catch (e: Exception){
            false
        }
    }
}