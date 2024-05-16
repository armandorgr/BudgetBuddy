package com.example.budgetbuddy.validations.validators

import android.content.Context
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.BaseValidator
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler

class GroupDescriptionValidator(private val context: Context) : BaseValidator(){
    override fun validate(input: Any): String? {
        val subject = context.getString(R.string.group_description_hint)
        val validator = BlankValidationHandler(
            context.getString(
                R.string.blank_validation_error,
                subject
            )
        )
            .setNext(
                LengthValidationHandler(
                    150,
                    context.getString(R.string.length_validation_error, subject, 150)
                ).setNext(
                    RegexValidationHandler(
                        ExpValidations.GROUP_DESCRIPTION,
                        context.getString(R.string.regex_validation_error, subject)
                    )
                )
            )
        return validator.validate(input)
    }
}