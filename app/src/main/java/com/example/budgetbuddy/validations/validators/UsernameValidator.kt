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
 * en este caso se usara para aplicar una series de validaciones al nombre de usuario.
 * */
class UsernameValidator (private val context: Context) : BaseValidator(){
    private val validator = BlankValidationHandler(context.getString(R.string.blank_validation_error, "The username"))
        .setNext(
            LengthValidationHandler(22, context.getString(R.string.length_validation_error, "The username", 22))
                .setNext(
                    RegexValidationHandler(
                        ExpValidations.USERNAME,
                        context.getString(R.string.regex_validation_error, "The username")
                    )
                )
        )
    override fun validate(input: Any): String? {
        return validator.validate(input)
    }
}