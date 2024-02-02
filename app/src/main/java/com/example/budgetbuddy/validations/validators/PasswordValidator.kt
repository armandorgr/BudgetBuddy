package com.example.budgetbuddy.validations.validators

import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler
import com.example.budgetbuddy.validations.SpaceValidationHandler

object PasswordValidator {
    private val validator = BlankValidationHandler("La contraseña no puede estar vacia")
        .setNext(
            SpaceValidationHandler("La contraseña no puede contener espacios")
                .setNext(
                    LengthValidationHandler(20, "La contraseña no puede tener mas de 20 caracteres")
                        .setNext(
                            RegexValidationHandler(
                                ExpValidations.PASSWORD,
                                "La contraseña no esta bien formada"
                            )
                        )
                )
        )

    fun validate(input: String): String? {
        return validator.validate(input)
    }
}