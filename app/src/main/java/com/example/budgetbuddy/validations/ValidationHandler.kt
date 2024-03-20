package com.example.budgetbuddy.validations

/**
 * Interfaz que define los metodos a implementar por un validador*/
interface ValidationHandler {
    /**
     * Metodo que sirve para definir el siguiente validador a usar en caso de que la entrada
     * pasa la validacion hecha en el metodo validate
     * @param handler cuyo metodo validar sera ejecutado si se pasa la validacion de este objeto
     * @return La misma instancia de este ValidationHandler
     * */
    fun setNext(handler: ValidationHandler): ValidationHandler
    /**
     * Metodo que sirve para validar la entrada, devolvera un mensaje de error de no pasar la validacion
     * o null de ser caso de estar correcto
     * @param input Entrada a validar
     * @return El resultado de validar, String si no pasa la validacion y null si lo hace.
     * */
    fun validate(input:Any):String?
}