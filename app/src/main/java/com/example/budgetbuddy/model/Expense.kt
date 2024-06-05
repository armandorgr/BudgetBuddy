package com.example.budgetbuddy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Expense(
    val title: String? = null,
    val amount: Double? = null,
    val date: String? = null,
    val payer: String? = null,
    val debt:  Double? = null,
    val payerUserName: String? = null,
) : Parcelable
