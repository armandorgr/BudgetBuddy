package com.example.budgetbuddy.validations

/**
 * Clase abstracta de la cual heredarán las clases que quieran implementar los metodos
 * para validar algun tipo de valor.
 * */
abstract class BaseValidationHandler : ValidationHandler{
    private var nextHandler: ValidationHandler? = null

    override fun setNext(handler: ValidationHandler): ValidationHandler {
        this.nextHandler = handler
        return this
    }

    override fun validate(input: Any): String? {
        return nextHandler?.validate(input)
    }
}