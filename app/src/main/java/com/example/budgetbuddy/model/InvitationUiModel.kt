package com.example.budgetbuddy.model

data class InvitationUiModel(
    val senderUid: String? = null,
    val senderUsername: String? = null,
    val text: String? = null,
    val type: INVITATION_TYPE? = null,
    var dateSent: String? = null
)