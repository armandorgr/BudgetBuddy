package com.example.budgetbuddy.model

import java.time.LocalDateTime

data class DateSent(
    var dateTime: String? = null
) {
    fun getLocalDateTime(): LocalDateTime? = dateTime?.let { LocalDateTime.parse(it) }
    fun setLocalDateTime(localDateTime: LocalDateTime) {
        dateTime = localDateTime.toString()
    }
}