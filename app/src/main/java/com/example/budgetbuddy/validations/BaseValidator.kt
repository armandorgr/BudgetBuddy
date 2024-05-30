package com.example.budgetbuddy.validations

/**
 * Clase abstracta de la cual heredarán todas los validadores de los campos
 * existentes dentro de la aplicación
 *
 * @author Armando Guzmáan
 * */
abstract class BaseValidator {

    /**
     * Metodo que funciona que para validar la entrada de texto
     * @param input [Any] valor a validar
     * @return resultado [String]? de validar, es nulo si no hay error.
     * */
    abstract fun validate(input:Any):String?
}