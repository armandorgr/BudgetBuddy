package com.example.budgetbuddy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Group(
    val creatorUid:String? = null,
    val name:String?=null,
    val description:String? = null,
    val startDate:String? = null,
    val endDate:String? = null,
    val members:Map<String, Boolean>? = null
) : Parcelable
