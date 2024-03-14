package com.example.budgetbuddy.validations

import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class LengthValidationHandler(private val maxLength: Int, private val message: String) :
    BaseValidationHandler() {
    override fun validate(input: String): String? {
        return if (input.trim().length > maxLength) {
            message
        } else {
            super.validate(input)
        }
    }
}

class BlankValidationHandler(private val message: String) : BaseValidationHandler() {
    override fun validate(input: String): String? {
        return if (input.trim().isEmpty() || input.isEmpty()) {
            message
        } else {
            super.validate(input)
        }
    }
}

class SpaceValidationHandler(private val message: String) : BaseValidationHandler() {
    override fun validate(input: String): String? {
        return if (input.trim().contains(" ")) {
            message
        } else {
            super.validate(input)
        }
    }
}

class DateLimitValidationHandler(private val limitDate: LocalDateTime, private val message: String) : BaseValidationHandler() {
    override fun validate(input: String): String? {
        return try{
                val convertedDate = LocalDateTime.parse(input)
                if(convertedDate.isBefore(limitDate)){
                    message
                }else{
                    null
                }
            }catch (e: DateTimeParseException){
                message
            }
    }
}

class RegexValidationHandler(private val exp: ExpValidations, private val message: String) :
    BaseValidationHandler() {
    override fun validate(input: String): String? {
        val exp = exp.exp
        return if (!Regex(exp).matches(input)) {
            message
        } else {
            super.validate(input)
        }
    }
}

enum class ExpValidations(val exp: String) {
    // Es posible tener maximo tres nombres como David Joaquin Juan
    NAMES("^[A-Za-záéíóúüñÁÉÍÓÚÜÑ]+(?: [A-Za-záéíóúüñÁÉÍÓÚÜÑ]+)?(?: [A-Za-záéíóúüñÁÉÍÓÚÜÑ]+)?$"),
    EMAIL("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"),
    PASSWORD("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@._-])[A-Za-z\\d@._-]{8,}$"),
    USERNAME("^(?=.*[a-zA-Z]{3})[a-zA-Z0-9-_$]{3,22}$"),
    GROUP_NAME("^[A-Za-záéíóúüñÁÉÍÓÚÜÑ0-9]+(?: [A-Za-záéíóúüñÁÉÍÓÚÜÑ0-9]+)?(?: [A-Za-záéíóúüñÁÉÍÓÚÜÑ0-9]+)?$"),
    GROUP_DESCRIPTION("^[a-zA-ZñÑáéíóúÁÉÍÓÚüÜ0-9 ]+$")
}
