package com.example.budgetbuddy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.UsersRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: UsersRepository
) : ViewModel(){
    val auth: FirebaseAuth = Firebase.auth

    private val _currentUser = MutableLiveData<User>()
    var currentUser: LiveData<User> = _currentUser

    private val _provider = MutableLiveData<String>()
    var provider: LiveData<String> = _provider

    private val _firebaseUser = MutableLiveData<FirebaseUser>()
    var firebaseUser:LiveData<FirebaseUser> = _firebaseUser

    fun updateUser(user: User){
        _currentUser.postValue(user)
    }

     suspend fun loadCurrentUser(){
         withContext(Dispatchers.IO){
             val usr = auth.currentUser!!
             _firebaseUser.postValue(usr)
             usr.uid.let {
                 val user = repo.findUserByUID(it)!!
                 user.profilePic = usr.photoUrl.toString()
                 Log.d("prueba", "profile pic: ${usr.photoUrl}")
                 _currentUser.postValue(user)
             }
             if(usr.providerData.size>0){
                 _provider.postValue(usr.providerData[usr.providerData.size - 1].providerId)
             }
         }
     }
}