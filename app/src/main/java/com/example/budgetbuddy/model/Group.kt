package com.example.budgetbuddy.model


data class Group(
    val creatorUid:String? = null,
    val name:String?=null,
    val description:String? = null,
    val startDate:String? = null,
    val endDate:String? = null,
    val members:Map<String, Boolean>? = null
)
