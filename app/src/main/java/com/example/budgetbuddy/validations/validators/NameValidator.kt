package com.example.budgetbuddy.validations.validators

import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler

object NameValidator {
    private val validator = BlankValidationHandler("El nombre no puede estar vacío")
        .setNext(
            LengthValidationHandler(30, "El nombre no puede tener más de 30 letras")
                .setNext(
                    RegexValidationHandler(
                        ExpValidations.NAMES,
                        "El nombre no esta bien formado"
                    )
                )
        )

    fun validate(input: String): String? {
        return validator.validate(input)
    }
}