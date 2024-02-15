package com.example.budgetbuddy

import com.example.budgetbuddy.validations.validators.EmailValidator
import com.example.budgetbuddy.validations.validators.NameValidator
import com.example.budgetbuddy.validations.validators.PasswordValidator
import com.example.budgetbuddy.validations.validators.UsernameValidator
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals


class CompleteValidationsTest {
    private lateinit var nameValidator:NameValidator
    private lateinit var emailValidator:EmailValidator
    private lateinit var passwordValidator:PasswordValidator
    private lateinit var usernameValidator: UsernameValidator

    /**
     * Funcion que se ejecuta justo despues de realizar los test, sirve
     * para inicializar los validatores declarados encima
     * */
    @Before
    fun setUp(){
        this.nameValidator = NameValidator
        this.emailValidator = EmailValidator
        this.passwordValidator = PasswordValidator
        this.usernameValidator = UsernameValidator
    }

    /**
     * Test que sirve para probar las validaciones de nombre y apellidos, en este caso se comprueban nombres validos
     * */
    @Test
    fun successNameValidationTest(){
        assertEquals(null, nameValidator.validate("armando guzman"))
        assertEquals(null, nameValidator.validate("armando"))
        assertEquals(null, nameValidator.validate("Armando Guzmán"))
        assertEquals(null, nameValidator.validate("David Joaquín Juan"))
        assertEquals(null, nameValidator.validate("Pepe"))
        assertEquals(null, nameValidator.validate("Guzmán Reyes"))
    }

    /**
     * Test que sirve para probar las validaciones de nombre y apellidos, en este caso se comprueban nombres invalidos
     * */
    @Test
    fun failNameValidationTest(){
        assert(nameValidator.validate("armando ") != null)
        assert(nameValidator.validate("armando guzman ") != null)
        assert(nameValidator.validate("armando4") != null)
        assert(nameValidator.validate("armando_") != null)
        assert(nameValidator.validate(" armando") != null)
        assert(nameValidator.validate(".armando ") != null)
    }

    /**
     * Test que sirve para validar que la validacion de nombre de usuario funciona correctamente, en este
     * caso se comprueban nombres validos
     * */
    @Test
    fun successUsernameValidationTest(){
        assertEquals(null, usernameValidator.validate("armandorgr1102_"))
        assertEquals(null, usernameValidator.validate("--Pepe--"))
        assertEquals(null, usernameValidator.validate("Alvaro123$"))
        assertEquals(null, usernameValidator.validate("LuisPepe"))
        assertEquals(null, usernameValidator.validate("Armando1102"))
    }

    /**
     * Test que sirve para validar que la validacion de nombre de usuario funciona correctamente, en este caso
     * se comprueban nombres invalidos
     * */
    @Test
    fun failUsernameValidationTest(){
        assert(usernameValidator.validate(" ") != null)
        assert(usernameValidator.validate("") != null)
        assert(usernameValidator.validate("Armando1102rgr1102_______") != null)
        assert(usernameValidator.validate("Armando@1102") != null)
        assert(usernameValidator.validate(" Armanodo1102 ") != null)
        assert(usernameValidator.validate(" Armanodo1102 ") != null)
        assert(usernameValidator.validate(" Armanodo1102 ") != null)
        assert(usernameValidator.validate("1102_$") != null)
        assert(usernameValidator.validate("1102") != null)
    }
}