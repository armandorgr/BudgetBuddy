package com.budgetbuddy

@kotlinx.serialization.Serializable
data class SubscribeRequest(
    val token: String,
    val topic: String
)