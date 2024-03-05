package com.example.budgetbuddy.viewmodels

import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: UsersRepository
) : ViewModel() {

    suspend fun findUser(uid:String): User?{
        return withContext(Dispatchers.IO){
            repo.findUserByUID(uid)
        }
    }
}