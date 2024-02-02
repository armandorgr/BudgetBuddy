package com.example.budgetbuddy

import com.example.budgetbuddy.validations.validators.EmailValidator
import com.example.budgetbuddy.validations.validators.NameValidator
import com.example.budgetbuddy.validations.validators.PasswordValidator
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals


class CompleteValidationsTest {
    private lateinit var nameValidator:NameValidator
    private lateinit var emailValidator:EmailValidator
    private lateinit var passwordValidator:PasswordValidator

    @Before
    fun setUp(){
        this.nameValidator = NameValidator
        this.emailValidator = EmailValidator
        this.passwordValidator = PasswordValidator
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
}