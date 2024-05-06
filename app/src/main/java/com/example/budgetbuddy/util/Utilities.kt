package com.example.budgetbuddy.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.ROLE

class Utilities {
    companion object{
        const val PROFILE_PIC_GG = "GG"
        const val PROFILE_PIC_ST = "ST"
        val ROLES_LIST = listOf(ROLE.MEMBER, ROLE.ADMIN)
        fun hideKeyboard(activity: Activity, context: Context){
            activity.currentFocus?.let { view ->
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}