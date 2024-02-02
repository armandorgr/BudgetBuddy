package com.example.budgetbuddy.validations.validators

import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler

object EmailValidator {
    private val validator = BlankValidationHandler("El email no puede estar vacío")
        .setNext(
            LengthValidationHandler(50, "El email no puede tener una longitud mayor de 50")
                .setNext(RegexValidationHandler(ExpValidations.EMAIL, "El email esta mal formado"))
        )

    fun validate(input: String): String? {
        return validator.validate(input)
    }
}