package com.example.budgetbuddy.validations.validators

import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler

/**
 * Objeto que sirve para validar el email
 * */
object EmailValidator {
    private val validator = BlankValidationHandler("El email no puede estar vacío")
        .setNext(
            LengthValidationHandler(50, "El email no puede tener una longitud mayor de 50")
                .setNext(RegexValidationHandler(ExpValidations.EMAIL, "El email esta mal formado"))
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