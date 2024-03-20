package com.example.budgetbuddy.validations

import java.time.LocalDateTime
import java.time.format.DateTimeParseException

/**
 * Clase que implementa la clase abstracta [BaseValidationHandler] viendose obligada a dar implementacion al metodo
 * [validate] el cual en este caso validara la longitud de una cadena pasada como parametro.
 * */
class LengthValidationHandler(private val maxLength: Int, private val message: String) :
    BaseValidationHandler() {
    override fun validate(input: Any): String? {
        require(input is String){
            "Expected input to be String $input"
        }
        return if (input.trim().length > maxLength) {
            message
        } else {
            super.validate(input)
        }
    }
}

/**
 * Clase que implementa la clase abstracta [BaseValidationHandler] viendose obligada a dar implementacion al metodo
 * [validate] el cual en este caso validara que la cadena pasada como parametro no este vacia
 * */
class BlankValidationHandler(private val message: String) : BaseValidationHandler() {
    override fun validate(input: Any): String? {
        require(input is String){
            "Expected input to be String $input"
        }
        return if (input.trim().isEmpty() || input.isEmpty()) {
            message
        } else {
            super.validate(input)
        }
    }
}

/**
 * Clase que implementa la clase abstracta [BaseValidationHandler] viendose obligada a dar implementacion al metodo
 * [validate] el cual en este caso validara que la cadena pasada como parametro no contiene espacios
 * */
class SpaceValidationHandler(private val message: String) : BaseValidationHandler() {
    override fun validate(input: Any): String? {
        require(input is String){
            "Expected input to be String $input"
        }
        return if (input.trim().contains(" ")) {
            message
        } else {
            super.validate(input)
        }
    }
}

/**
 * Clase que implementa la clase abstracta [BaseValidationHandler] viendose obligada a dar implementacion al metodo
 * [validate] el cual en este caso validara que la fecha pasada como parametro no sea anterior a otra fecha limite.
 * */
class DateLimitValidationHandler(private val limitDate: LocalDateTime, private val message: String) : BaseValidationHandler() {
    override fun validate(input: Any): String? {
        require(input is LocalDateTime){
            "Expected input to be LocalDateTime $input"
        }
        return try{
                if(input.isBefore(limitDate)){
                    message
                }else{
                    null
                }
            }catch (e: DateTimeParseException){
                message
            }
    }
}

/**
 * Clase que implementa la clase abstracta [BaseValidationHandler] viendose obligada a dar implementacion al metodo
 * [validate] el cual en este caso validara el formato de una cadena basado en una de las expresiones regulares definidas en la clase
 * [ExpValidations]
 * */
class RegexValidationHandler(private val exp: ExpValidations, private val message: String) :
    BaseValidationHandler() {
    override fun validate(input: Any): String? {
        require(input is String){
            "Expected input to be String $input"
        }
        val exp = exp.exp
        return if (!Regex(exp).matches(input)) {
            message
        } else {
            super.validate(input)
        }
    }
}

/**
 * Enumeraciono la cual define una serie de expresiones regulares las cuales seran usaddas para validar el formato
 * de ciertos datos mediante la clase [RegexValidationHandler]
 * */
enum class ExpValidations(val exp: String) {
    // Es posible tener maximo tres nombres como David Joaquin Juan
    NAMES("^[A-Za-záéíóúüñÁÉÍÓÚÜÑ]+(?: [A-Za-záéíóúüñÁÉÍÓÚÜÑ]+)?(?: [A-Za-záéíóúüñÁÉÍÓÚÜÑ]+)?$"),
    EMAIL("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"),
    PASSWORD("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@._-])[A-Za-z\\d@._-]{8,}$"),
    USERNAME("^(?=.*[a-zA-Z]{3})[a-zA-Z0-9-_$]{3,22}$"),
    GROUP_NAME("^[A-Za-záéíóúüñÁÉÍÓÚÜÑ0-9]+(?: [A-Za-záéíóúüñÁÉÍÓÚÜÑ0-9]+)?(?: [A-Za-záéíóúüñÁÉÍÓÚÜÑ0-9]+)?$"),
    GROUP_DESCRIPTION("^[a-zA-ZñÑáéíóúÁÉÍÓÚüÜ0-9 ]+$")
}
