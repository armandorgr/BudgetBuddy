package com.example.budgetbuddy.viewmodels

import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.InvitationUiModel
import com.example.budgetbuddy.repositories.InvitationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvitationsViewModel @Inject constructor(
    private val repo: InvitationsRepository
) : ViewModel(){
    suspend fun writeInvitation(uid:String, invitation:InvitationUiModel){
        repo.writeNewInvitation(uid, invitation)
    }
}