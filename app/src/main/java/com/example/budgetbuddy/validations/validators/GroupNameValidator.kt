package com.example.budgetbuddy.validations.validators

import android.content.Context
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler

class GroupNameValidator(private val context: Context){
    private val validator = BlankValidationHandler(
        context.getString(
            R.string.blank_validation_error,
            "The group name"
        )
    ).setNext(
        LengthValidationHandler(
            25,
            context.getString(R.string.length_validation_error, "The group name", 25)
        )
            .setNext(
                RegexValidationHandler(
                    ExpValidations.GROUP_NAME,
                    context.getString(R.string.regex_validation_error, "The group name")
                )
            )
    )

    /**
     * Metodo que funciona que para validar la entrada de texto
     * @param input [String] valor a validar
     * @return resultado [String]? de validar, es nulo si no hay error.
     * */
    fun validate(input: String): String? {
        return validator.validate(input)
    }


}