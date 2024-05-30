package com.example.budgetbuddy.validations.validators

import android.content.Context
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.BaseValidator
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler

class NewExpenseTitleValidator(private val context: Context): BaseValidator() {
    private val validator = BlankValidationHandler(
        context.getString(
            R.string.blank_validation_error,
            "The expense title"
        )
    ).setNext(
        LengthValidationHandler(
            20,
            context.getString(R.string.length_validation_error, "The expense title", 20)
        )
            .setNext(
                RegexValidationHandler(
                    ExpValidations.EXPENSE_TITLE,
                    context.getString(R.string.regex_validation_error, "The expense title")
                )
            )
    )

    /**
     * Metodo que funciona que para validar la entrada de texto
     * @param input [String] valor a validar
     * @return resultado [String]? de validar, es nulo si no hay error.
     * */
    override fun validate(input: Any): String? {
        return validator.validate(input)
    }

}