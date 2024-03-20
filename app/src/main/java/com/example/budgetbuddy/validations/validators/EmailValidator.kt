package com.example.budgetbuddy.validations.validators

import android.content.Context
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.BaseValidator
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler

/**
 * Clase que implementa la clase abstrcta [BaseValidator], viendose obligada a implementar el metodo [validate]
 * en este caso se usara para aplicar una series de validaciones al email..
 * */
class EmailValidator(private val context: Context) : BaseValidator(){
    private val validator = BlankValidationHandler(context.getString(R.string.blank_validation_error, "The email"))
        .setNext(
            LengthValidationHandler(50, context.getString(R.string.length_validation_error, "The email", 50))
                .setNext(RegexValidationHandler(ExpValidations.EMAIL, context.getString(R.string.regex_validation_error, "The email")))
        )

    override fun validate(input: Any): String? {
        return validator.validate(input)
    }
}