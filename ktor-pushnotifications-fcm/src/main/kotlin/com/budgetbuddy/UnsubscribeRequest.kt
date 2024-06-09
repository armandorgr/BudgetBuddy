package com.budgetbuddy

@kotlinx.serialization.Serializable
data class UnsubscribeRequest(
    val tokens: MutableList<String>,
    val topic: String
)
