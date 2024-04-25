package com.example.budgetbuddy.validations.validators

import android.content.Context
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.BaseValidator
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.LengthValidationHandler

class MessageValidator (private val context: Context): BaseValidator(){
    override fun validate(input: Any): String? {
        val validator = BlankValidationHandler(context.getString(R.string.blank_message_validation_error)).setNext(
            LengthValidationHandler(200, context.getString(R.string.length_message_validation_error, 200))
        )
        return validator.validate(input)
    }
}