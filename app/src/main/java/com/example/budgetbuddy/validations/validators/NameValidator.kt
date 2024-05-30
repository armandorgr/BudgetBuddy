package com.example.budgetbuddy.validations.validators

import android.content.Context
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.BaseValidator
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler
import com.example.budgetbuddy.validations.ValidationHandler

/**
 * Clase que implementa la clase abstrcta [BaseValidator], viendose obligada a implementar el metodo [validate]
 * en este caso se usara para aplicar una series de validaciones al nombre.
 * @param context Contexto usado para acceder a los recursos de la aplicación
 * @param isLastName Valor que indica si el mensaje usado de respuesta debe hacer referencia a los nombres o a los
 * apellidos
 *
 * @author Armando Guzmán
 * */
class NameValidator(private val context: Context, private val isLastName:Boolean = false) : BaseValidator(){
    override fun validate(input: Any): String? {
        val subject = if(!isLastName) context.getString(R.string.first_name) else context.getString(R.string.last_name)
        val validator:ValidationHandler = BlankValidationHandler(context.getString(R.string.blank_validation_error, subject))
            .setNext(
                LengthValidationHandler(30, context.getString(R.string.length_validation_error, subject, 30))
                    .setNext(
                        RegexValidationHandler(
                            ExpValidations.NAMES,
                            context.getString(R.string.regex_validation_error,subject)
                        )
                    )
            )
        return validator.validate(input)
    }
}