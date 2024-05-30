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
 * en este caso se usara para aplicar una series de validaciones al descripción de grupo
 *
 * @author Armando Guzmán
 * */
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