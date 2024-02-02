package com.example.budgetbuddy.validations

interface ValidationHandler {
    fun setNext(handler: ValidationHandler): ValidationHandler
    fun validate(input:String):String?
}