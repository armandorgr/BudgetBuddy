package com.example.budgetbuddy.validations.validators

import android.content.Context
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.BaseValidator
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler

class GroupDescriptionValidator(private val context: Context) : BaseValidator(){
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

    override fun validate(input: Any): String? {
        return validator.validate(input)
    }
}