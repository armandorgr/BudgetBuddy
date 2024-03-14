package com.example.budgetbuddy.validations.validators

import android.content.Context
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler

class GroupDescriptionValidator(private val context: Context) {
    private val validator = BlankValidationHandler(
        context.getString(
            R.string.blank_validation_error,
            "The group description"
        )
    )
        .setNext(
            LengthValidationHandler(
                150,
                context.getString(R.string.length_validation_error, "The group description", 150)
            ).setNext(
                RegexValidationHandler(
                    ExpValidations.GROUP_DESCRIPTION,
                    context.getString(R.string.regex_validation_error, "The group description")
                )
            )
        )

    fun validate(input: String): String? {
        return validator.validate(input)
    }
}