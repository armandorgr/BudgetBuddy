package com.example.budgetbuddy.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

class Utilities {
    companion object{
        fun hideKeyboard(activity: Activity, context: Context){
            activity.currentFocus?.let { view ->
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}