package com.example.budgetbuddy.viewmodels

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.budgetbuddy.R
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.UsersRepository
import com.example.budgetbuddy.validations.validators.NameValidator
import com.example.budgetbuddy.validations.validators.EmailValidator
import com.example.budgetbuddy.validations.validators.PasswordValidator
import com.example.budgetbuddy.validations.validators.UsernameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * ViewModel encargado del comportamiento del proceso de registro de nuevos usuarios.
 * */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: UsersRepository
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

    val personalDataGood: Boolean
        get(){
            return userNameError.value.equals("") &&
                    firstNameError.value.equals("") &&
                    lastNameError.value.equals("")
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
     * @param context [Context] Contexto para obtener recursos de String
     * @return Resultado de validar
     * */
    fun validateUserName(input: String, context: Context): String? {
        val usernameValidator = UsernameValidator(context)
        val response:String? = usernameValidator.validate(input)
        _usernameError.postValue( response ?: "")
        return response
    }

    /**
     * Metodo que sirve para validar el email y mostrar un error descriptivo en pantalla.
     * @param input [String]
     * @param context [Context] Contexto para obtener recursos de String
     * @return resultado de validar
     * */
    fun validateEmail(input: String, context: Context):String? {
        val emailValidator = EmailValidator(context)
        val response:String? = emailValidator.validate(input)
        _emailError.postValue( response ?: "")
        return response
    }

    /**
     * Metodo que sirva para validar la contraseña y mostrar un error descriptivo en pantalla
     * @param password [String] primera contraseña a validar
     * @param repeatPassword [String] segunda contraseña que sirve para validar que la primera y esta coinciden
     * @param context [Context] Contexto para obtener recursos de String
     * @return resultado de validar
     * */
    fun validatePassword(password: String, repeatPassword: String, context: Context):String? {
        val passwordValidator = PasswordValidator(context)
        val response:String? = passwordValidator.validate(password)
        _passwordError.postValue(
             response ?: validateRepeatPassword(
                password,
                repeatPassword,
                context
            )
        )
        return response
    }

    /**
     * Metodo auxiliar que sirve para validar que las dos contraseñas coinciden
     * @param input [String] primera contrasña a validar
     * @param input2 [String] segunda contraseña a validar que coincide con la primera
     * @param context [Context] Contexto para obtener recursos de String
     * @return [Unit]
     * */
    private fun validateRepeatPassword(input: String, input2: String, context: Context): String {
        return if (!(input.contentEquals(input2))) {
            context.getString(R.string.passwords_are_not_the_same)
        } else {
            ""
        }
    }

    /**
     * Metodo que sirva para validar el nombre y mostrar un error descriptivo en pantalla
     * @param input [String] nombre a validar
     * @param context [Context] Contexto para obtener recursos de String
     * @return resultado de validar
     * */
    fun validateFirstName(input: String, context: Context):String? {
        val nameValidator = NameValidator(context)
        val response:String? = nameValidator.validate(input)
        _firstNameError.postValue( response ?: "")
        return response
    }

    /**
     * Metodo que sirva para validar los apellidos y mostrar un error descriptivo en pantalla
     * @param input [String] apellidos a validar
     * @param context [Context] Contexto para obtener recursos de String
     * @return resultado de validar
     * */
    fun validateLastName(input: String, context: Context):String? {
        val nameValidator = NameValidator(context)
        val response:String? = nameValidator.validate(input)
        _lastNameError.postValue( response ?: "")
        return response
    }
    suspend fun findUser(username: String): User? {
        return withContext(Dispatchers.IO){
            repo.findUserByUserName(username)
        }
    }

    suspend fun findUserByUID(uid: String): User?{
        return withContext(Dispatchers.IO){
            repo.findUserByUID(uid)
        }
    }

    suspend fun createNewUser(user:User, uid:String):Boolean{
        return suspendCoroutine { continuation ->
            repo.writeNewUser(user, uid).addOnCompleteListener {
                if (it.isSuccessful) {
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        }
    }

    /**
     * Método usado para crear un nuevo usuario en la base de datos
     * @param uid Uid del usuario a guardar en la base de datos.
     * */
    suspend fun createNewUser(uid: String): Boolean {
        val user = User(
            _firstName.value.toString(),
            _lastName.value.toString(),
            _username.value.toString()
        )

        return suspendCoroutine { continuation ->
            repo.writeNewUser(uid, user).addOnCompleteListener {
                if (it.isSuccessful) {
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        }
    }

}