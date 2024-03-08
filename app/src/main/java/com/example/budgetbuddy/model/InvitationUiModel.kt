package com.example.budgetbuddy.model

import java.time.LocalDateTime

data class InvitationUiModel(
    val senderUid: String,
    val text:String,
    val type: INVITATION_TYPE,
    val dateSent: LocalDateTime
)