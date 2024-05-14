package com.example.budgetbuddy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class Group(
    var pic:String? = null,
    val creatorUid:String? = null,
    val name:String?=null,
    val description:String? = null,
    val startDate:String? = null,
    val endDate:String? = null,
    val members:Map<String, ROLE>? = null,
    val lastUpdated:Long?=null,
    val category: GROUP_CATEGORY? = null
) : Parcelable
