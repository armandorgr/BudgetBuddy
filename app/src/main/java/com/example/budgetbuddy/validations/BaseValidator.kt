package com.example.budgetbuddy.validations

abstract class BaseValidator {

    /**
     * Metodo que funciona que para validar la entrada de texto
     * @param input [Any] valor a validar
     * @return resultado [String]? de validar, es nulo si no hay error.
     * */
    abstract fun validate(input:Any):String?
}