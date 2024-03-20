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
 * en este caso se usara para aplicar una series de validaciones al nombre.
 * */
class NameValidator(private val context: Context) : BaseValidator(){
    private val validator = BlankValidationHandler(context.getString(R.string.blank_validation_error, "The name"))
        .setNext(
            LengthValidationHandler(30, context.getString(R.string.length_validation_error, "The name", 30))
                .setNext(
                    RegexValidationHandler(
                        ExpValidations.NAMES,
                        context.getString(R.string.regex_validation_error,"The name")
                    )
                )
        )
    override fun validate(input: Any): String? {
        return validator.validate(input)
    }
}