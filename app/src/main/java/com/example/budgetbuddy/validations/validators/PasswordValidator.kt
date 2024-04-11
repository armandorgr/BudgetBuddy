package com.example.budgetbuddy.validations.validators

import android.content.Context
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.BaseValidator
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler
import com.example.budgetbuddy.validations.SpaceValidationHandler

/**
 * Clase que implementa la clase abstrcta [BaseValidator], viendose obligada a implementar el metodo [validate]
 * en este caso se usara para aplicar una series de validaciones a la contraseña.
 * */
class PasswordValidator (private val context: Context) : BaseValidator(){
    private val validator = BlankValidationHandler(context.getString(R.string.blank_validation_error, "The password"))
        .setNext(
            SpaceValidationHandler(context.getString(R.string.space_validation_error, "The password"))
                .setNext(
                    LengthValidationHandler(20, context.getString(R.string.length_validation_error, "The password", 20))
                        .setNext(
                            RegexValidationHandler(
                                ExpValidations.PASSWORD,
                                context.getString(R.string.regex_validation_error, "The password")
                            )
                        )
                )
        )
    override fun validate(input: Any): String? {
        return validator.validate(input)
    }
}