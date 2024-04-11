package com.example.budgetbuddy.model

data class InvitationUiModel(
    val pic:String? = null,
    val senderUid: String? = null,
    val senderName: String? = null,
    val type: INVITATION_TYPE? = null,
    var dateSent: String? = null
)