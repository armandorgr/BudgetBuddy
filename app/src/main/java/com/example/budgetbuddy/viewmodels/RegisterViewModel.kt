package com.example.budgetbuddy.viewmodels

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.budgetbuddy.R
import com.example.budgetbuddy.validations.validators.NameValidator
import com.example.budgetbuddy.validations.validators.EmailValidator
import com.example.budgetbuddy.validations.validators.PasswordValidator
import com.example.budgetbuddy.validations.validators.UsernameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel encargado del comportamiento del proceso de registro de nuevos usuarios.
 * */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val nameValidator: NameValidator,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val usernameValidator: UsernameValidator
) :
    ViewModel() {

    val allGood: Boolean
        get() {
            return userNameError.value.equals("") &&
                    emailError.value.equals("") &&
                    firstNameError.value.equals("") &&
                    lastNameError.value.equals("") &&
                    passwordError.value.equals("")
        }

    private val _username = MutableLiveData<String>()
    var username: LiveData<String> = _username

    private val _email = MutableLiveData<String>()
    var email: LiveData<String> = _email

    private val _firstName = MutableLiveData<String>()
    var firstName: LiveData<String> = _firstName

    private val _lastName = MutableLiveData<String>()
    var lastName: LiveData<String> = _lastName

    private val _password = MutableLiveData<String>()
    var password: LiveData<String> = _password

    private val _repeatPassword = MutableLiveData<String>()
    var repeatPassword: LiveData<String> = _repeatPassword

    private val _firstNameError = MutableLiveData<String>()
    val firstNameError: LiveData<String> = _firstNameError

    private val _usernameError = MutableLiveData<String>()
    val userNameError: LiveData<String> = _usernameError

    private val _lastNameError = MutableLiveData<String>()
    val lastNameError: LiveData<String> = _lastNameError

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String> = _emailError

    private val _passwordError = MutableLiveData<String>()
    val passwordError: LiveData<String> = _passwordError


    fun setUserName(username: String) {
        this._username.postValue(username)
    }

    fun setEmail(email: String) {
        this._email.postValue(email)
    }

    fun setFirstName(firstName: String) {
        this._firstName.postValue(firstName)
    }

    fun setLastName(lastName: String) {
        this._lastName.postValue(lastName)
    }

    fun setPassword(password: String) {
        this._password.postValue(password)
    }

    fun setRepeatPassword(repeatPassword: String) {
        this._repeatPassword.postValue(repeatPassword)
    }


    /**
     * Metodo que sirve para validar el nombre de usuario y mostrar un error descriptivo en pantalla.
     * @param input [String] valor a validar
     * @return [Unit]
     * */
    fun validateUserName(input: String) {
        _usernameError.postValue(usernameValidator.validate(input) ?: "")
    }

    /**
     * Metodo que sirve para validar el email y mostrar un error descriptivo en pantalla.
     * @param input [String]
     * @return [Unit]
     * */
    fun validateEmail(input: String) {
        _emailError.postValue(emailValidator.validate(input) ?: "")
    }

    /**
     * Metodo que sirva para validar la contraseña y mostrar un error descriptivo en pantalla
     * @param password [String] primera contraseña a validar
     * @param repeatPassword [String] segunda contraseña que sirve para validar que la primera y esta coinciden
     * @return [Unit]
     * */
    fun validatePassword(password: String, repeatPassword: String) {
        _passwordError.postValue(
            passwordValidator.validate(password) ?: validateRepeatPassword(
                password,
                repeatPassword
            )
        )
    }

    /**
     * Metodo auxiliar que sirve para validar que las dos contraseñas coinciden
     * @param input [String] primera contrasña a validar
     * @param input2 [String] segunda contraseña a validar que coincide con la primera
     * @return [Unit]
     * */
    private fun validateRepeatPassword(input: String, input2: String): String {
        return if (!(input.contentEquals(input2))) {
            "Las contraseñas no coinciden"
        } else {
            ""
        }
    }

    /**
     * Metodo que sirva para validar el nombre y mostrar un error descriptivo en pantalla
     * @param input [String] nombre a validar
     * @return [Unit]
     * */
    fun validateFirstName(input: String) {
        _firstNameError.postValue(nameValidator.validate(input) ?: "")
    }

    /**
     * Metodo que sirva para validar los apellidos y mostrar un error descriptivo en pantalla
     * @param input [String] apellidos a validar
     * @return [Unit]
     * */
    fun validateLastName(input: String) {
        _lastNameError.postValue(nameValidator.validate(input) ?: "")
    }

    /**
     * Metodo que sirve para moverse desde la pantalla de registro a la de login
     * @return [View.OnClickListener] funcion usada para realizar el cambio de pantalla
     * */
    fun moveToLogin(): View.OnClickListener {
        return Navigation.createNavigateOnClickListener(R.id.nav_register_to_login, null)
    }

    /**
     * Metodo que sirve para moverse desde la pantalla de login a la de registro
     * @return [View.OnClickListener] funcion usada para realizar el cambio de pantalla
     * */
    fun moveToRegister(): View.OnClickListener {
        return Navigation.createNavigateOnClickListener(R.id.nav_login_to_register, null)
    }

}