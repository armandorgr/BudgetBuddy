package com.example.budgetbuddy.validations

abstract class BaseValidator {
    abstract fun validate(input:String):String?
}