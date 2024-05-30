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
 * en este caso se usara para aplicar una series de validaciones al nombre del grupo
 * @param context Contexto usaodo para acceder a los recursos de la aplicación
 *
 * @author Armando Guzmán
 * */
class GroupNameValidator(private val context: Context):BaseValidator(){

    override fun validate(input: Any): String? {
        val subject = context.getString(R.string.group_name_hint)
        val validator = BlankValidationHandler(
            context.getString(
                R.string.blank_validation_error,
                subject
            )
        ).setNext(
            LengthValidationHandler(
                25,
                context.getString(R.string.length_validation_error, subject, 25)
            )
                .setNext(
                    RegexValidationHandler(
                        ExpValidations.GROUP_NAME,
                        context.getString(R.string.regex_validation_error, subject)
                    )
                )
        )
        return validator.validate(input)
    }


}