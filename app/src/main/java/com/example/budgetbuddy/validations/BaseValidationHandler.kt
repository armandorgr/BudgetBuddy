package com.example.budgetbuddy.validations

abstract class BaseValidationHandler : ValidationHandler{
    private var nextHandler: ValidationHandler? = null

    override fun setNext(handler: ValidationHandler): ValidationHandler {
        this.nextHandler = handler
        return this
    }

    override fun validate(input: String): String? {
        return nextHandler?.validate(input)
    }
}