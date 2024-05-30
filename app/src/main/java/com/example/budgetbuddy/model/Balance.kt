package com.example.budgetbuddy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Balance (
    val user1 : String ? = null,
    val user2 : String? = null,
    val amountUser1: Double? = null,
    val amountUser2: Double? = null,
): Parcelable