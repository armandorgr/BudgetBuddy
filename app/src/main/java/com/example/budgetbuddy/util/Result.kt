package com.example.budgetbuddy.util

import androidx.annotation.DrawableRes

data class Result(
    val title: String,
    val text: String,
    val btnText: String,
    val onClick: () -> Unit
)
