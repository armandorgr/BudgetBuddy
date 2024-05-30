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
 * @param context Contexto usado para acceder a los recursos del sistema
 *
 * @author Armando Guzmán
 * */
class PasswordValidator (private val context: Context) : BaseValidator(){

    override fun validate(input: Any): String? {
        val subject = context.getString(R.string.password)
        val validator = BlankValidationHandler(context.getString(R.string.blank_validation_error, subject))
            .setNext(
                SpaceValidationHandler(context.getString(R.string.space_validation_error, subject))
                    .setNext(
                        LengthValidationHandler(20, context.getString(R.string.length_validation_error, subject, 20))
                            .setNext(
                                RegexValidationHandler(
                                    ExpValidations.PASSWORD,
                                    context.getString(R.string.regex_validation_error, subject)
                                )
                            )
                    )
            )
        return validator.validate(input)
    }
}