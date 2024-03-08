package com.example.budgetbuddy.viewmodels

import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.UsersRepository
import com.example.budgetbuddy.validations.validators.EmailValidator
import com.example.budgetbuddy.validations.validators.PasswordValidator
import com.example.budgetbuddy.validations.validators.UsernameValidator
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val emailValidator: EmailValidator,
    private val usernameValidator: UsernameValidator,
    private val passwordValidator: PasswordValidator
) : ViewModel() {

    suspend fun findUser(uid: String): User? {
        return withContext(Dispatchers.IO) {
            repo.findUserByUID(uid)
        }
    }

    suspend fun findUserByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            repo.findUserByUserName(username)
        }
    }

    suspend fun deleteUser(uid: String): Boolean {
        return withContext(Dispatchers.IO) {
            repo.deleteUser(uid)
        }
    }

    fun updateUsername(uid: String, newUsername: String): Task<Void> {
        return repo.updateUsername(uid, newUsername)
    }

    fun validateUsername(input: String): String? {
        return usernameValidator.validate(input)
    }

    fun validatePassword(input: String): String? {
        return passwordValidator.validate(input)
    }

    fun validateEmail(input: String): String? {
        return emailValidator.validate(input)
    }
}