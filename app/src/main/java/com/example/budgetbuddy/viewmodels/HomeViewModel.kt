package com.example.budgetbuddy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.UsersRepository
import com.example.budgetbuddy.util.Utilities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel en el cual de define la lógica para cargar los datos del usuario que actualmenta
 * ha iniciado sesión
 * @param repo Repositorio de usuarios
 * Uso de [ViewModel] consultado aquí:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Android Architecture Components - ViewModel
 *  La forma de trabajar con la autenticación de Firebase fue consultada en la documentacion de Firebase: https://firebase.google.com/docs/auth/android/start
 * @author Armando Guzmán
 * */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: UsersRepository
) : ViewModel() {
    val auth: FirebaseAuth = Firebase.auth

    private val _currentUser: MutableLiveData<User> = MutableLiveData<User>()
    var currentUser: LiveData<User> = _currentUser

    private val _provider: MutableLiveData<String> = MutableLiveData<String>()
    var provider: LiveData<String> = _provider

    private val _firebaseUser: MutableLiveData<FirebaseUser> = MutableLiveData<FirebaseUser>()
    var firebaseUser: LiveData<FirebaseUser> = _firebaseUser

    /**
     * Método que sirve actualizar el valor del usuario actual
     * @param user Usuario a establecer como usuario actual
     * */
    fun updateUser(user: User) {
        _currentUser.postValue(user)
    }

    /**
     * Método que sirve para cargar los datos del usuario actual
     * */
    suspend fun loadCurrentUser() {
        withContext(Dispatchers.IO) {
            val usr = auth.currentUser!!
            _firebaseUser.postValue(usr)
            usr.uid.let {
                val user = repo.findUserByUID(it)!!
                if (usr.photoUrl != null) {
                    val photoUrl = Utilities.PROFILE_PIC_GG + usr.photoUrl.toString()
                    user.profilePic = photoUrl
                    repo.setProfilePic(photoUrl, usr.uid)
                }
                _currentUser.postValue(user)
            }
            if (usr.providerData.size > 0) {
                _provider.postValue(usr.providerData[usr.providerData.size - 1].providerId)
            }
        }
    }
}