package com.example.budgetbuddy.model

data class Message(
    val text: String? = null,
    val senderUID: String? = null,
    val sentDate: Long? = null,
    val type: MESSAGE_TYPE? = null,
    val imgPath: String? = null
)