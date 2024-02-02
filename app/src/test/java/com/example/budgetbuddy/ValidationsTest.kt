package com.example.budgetbuddy

import com.example.budgetbuddy.validations.BaseValidationHandler
import com.example.budgetbuddy.validations.BlankValidationHandler
import com.example.budgetbuddy.validations.ExpValidations
import com.example.budgetbuddy.validations.LengthValidationHandler
import com.example.budgetbuddy.validations.RegexValidationHandler
import com.example.budgetbuddy.validations.SpaceValidationHandler
import org.junit.Assert.*
import org.junit.Test

/**
 * Clase de pruebas para validar las validaciones.
 * */
class ValidationsTest {
    private lateinit var validationHandler: BaseValidationHandler

    /**
     * Prueba que el validador de longitud devuelve null cuando la cadena no supera la longitud maxima
     * */
    @Test
    fun successLengthValidationTest() {
        validationHandler = LengthValidationHandler(4, "Cadena tiene mas de 4 letras")
        assertEquals(null, validationHandler.validate("mar"))
    }

    /**
     * Prueba que el validador de longitud si devuelve el mensaje cuando la cadena se pasa del maximo de letra.
     * */
    @Test
    fun failedLengthValidationTest() {
        validationHandler = LengthValidationHandler(5, "Cadena tiene mas de 5 letras")
        assert(validationHandler.validate("mas de 5 letras") != null)
    }

    /**
     * Prueba que el validador solo coge letra, los espacios los ignora.
     * */
    @Test
    fun emptyLengthValidationTest() {
        validationHandler = LengthValidationHandler(5, "Cadena tiene mas de 5 letras")
        assertEquals(null, validationHandler.validate("                                 "))
    }

    /**
     * Prueba que el validador devuelve null cuadno la cadena no esta vacia
     * */
    @Test
    fun successBlankValidationTest(){
        validationHandler = BlankValidationHandler("La cadena esta vacia")
        assertEquals(null, validationHandler.validate("pepe"))
        assertEquals(null, validationHandler.validate("  pepe  "))
    }

    /**
     *  Prueba que el validador devuelve el mensaje cuando la cadena esta vacia
     * */
    @Test
    fun failedBlankValidationTest(){
        validationHandler = BlankValidationHandler("La cadena esta vacia")
        assert(validationHandler.validate("         ") != null)
        assert(validationHandler.validate("") != null)
    }

    /**
     * Prueba que el validador devuelve null cuando la cadena no contiene espacios de por medio
     * */
    @Test
    fun successSpaceValidationTest(){
        validationHandler = SpaceValidationHandler("La cadena no puede contener espacios")
        assertEquals(null, validationHandler.validate("armando1102"))
    }

    /**
     * Prueba que el validador devuelve el mensaje cuundo la cadena si tiene espacios entre palabras.
     * */
    @Test
    fun failedSpaceValidationTest(){
        validationHandler = SpaceValidationHandler("La cadena no puede contener espacio")
        assert(validationHandler.validate("armando guzman") != null)
        assert(validationHandler.validate("  armando guzman ") != null)
    }

    @Test
    fun successNameValidationTest(){
        validationHandler = RegexValidationHandler(ExpValidations.NAMES, "No cumple con el formato")
        assertEquals(null, validationHandler.validate("armando"))
        assertEquals(null, validationHandler.validate("armando guzman"))
        assertEquals(null, validationHandler.validate("Armando Guzmán"))
        assertEquals(null, validationHandler.validate("Armando g"))
    }

    @Test
    fun failedNameValidationTest(){
        validationHandler = RegexValidationHandler(ExpValidations.NAMES, "No cumple con el formato")
        assert(validationHandler.validate(" ") != null)
        assert(validationHandler.validate("Armando ") != null)
        assert(validationHandler.validate(" Armando") != null)
        assert(validationHandler.validate(" Armando12") != null)
    }

    @Test
    fun successEmailValidationTest(){
        validationHandler = RegexValidationHandler(ExpValidations.EMAIL, "No cumple con el formato")
        assertEquals(null, validationHandler.validate("armandorgr1102@gmail.com"))
        assertEquals(null, validationHandler.validate("armando.guzman1102@gmail.com"))
        assertEquals(null, validationHandler.validate("AGUZMANR427@alumnos.imf.com"))
        assertEquals(null, validationHandler.validate("AGUZM_NR427@alumnos.imf.com"))
        assertEquals(null, validationHandler.validate("AGUZM%NR427@alumnos.imf.es"))
    }

    @Test
    fun failedEmailValidationTest(){
        validationHandler = RegexValidationHandler(ExpValidations.EMAIL, "No cumpple con el formato")
        assert(validationHandler.validate("armando rgr1102@gmail.com") != null)
        assert(validationHandler.validate("ar@mandorgr1102@gmail.com") != null)
        assert(validationHandler.validate(" armandorgr1102@gmail.com") != null)
        assert(validationHandler.validate("armando rgr1102@gmail.com ") != null)
        assert(validationHandler.validate("armando;rgr1102@gmail.com") != null)
        assert(validationHandler.validate("armandorgr1102@gmail.") != null)
        assert(validationHandler.validate("armandorgr1102@gmailcom") != null)
        assert(validationHandler.validate("armandorgr1102@gmailcom.") != null)
        assert(validationHandler.validate(".armandorgr1102@gmailcom.es") != null)
        assert(validationHandler.validate("arman..dorgr1102@gmailcom.es") != null)
    }

    @Test
    fun successPasswordValidationTest(){
        validationHandler = RegexValidationHandler(ExpValidations.PASSWORD, "No cumple con el formato")
        assertEquals(null, validationHandler.validate("Xp1_ko5g"))
        assertEquals(null, validationHandler.validate("7Mnajsk@"))
    }

    @Test
    fun failedPasswordValidationTest(){
        validationHandler = RegexValidationHandler(ExpValidations.PASSWORD, "No comple con el formato")
        assert(validationHandler.validate("") != null)
        assert(validationHandler.validate("armando") != null)
        assert(validationHandler.validate("12345") != null)
        assert(validationHandler.validate("Ag1r4hcb") != null)
        assert(validationHandler.validate("Ag-r_hcb") != null)
        assert(validationHandler.validate("Ag-r4 _hcb") != null)
        assert(validationHandler.validate("Ag4_") != null)
    }
}