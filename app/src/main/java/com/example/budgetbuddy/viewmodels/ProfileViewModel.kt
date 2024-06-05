package com.example.budgetbuddy.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.example.budgetbuddy.http.FcmAPI
import com.example.budgetbuddy.model.ListItemUiModel
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.StorageRepository
import com.example.budgetbuddy.repositories.UsersRepository
import com.example.budgetbuddy.util.ListItemImageLoader
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel en el cual de define la lógica para todas las acciones relacionadas con el perfil del usuario actual
 * @param repo Repositorio de usuarios
 * @param storageRepository Repositorio de Firebase Storage
 * Uso de [ViewModel] consultado aquí:
 * https://www.packtpub.com/product/how-to-build-android-apps-with-kotlin-second-edition/9781837634934
 * capítulo Android Architecture Components - ViewModel
 * @author Armando Guzmán
 * */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val storageRepository: StorageRepository,
    private val apiService: FcmAPI
) : ViewModel() {

    /**
     * Método que sirve para encontrar un usuario mediante su UID
     * @param uid UID del usuario a buscar
     * @return El usuario encontrado, si no existe será nulo
     * */
    suspend fun findUser(uid: String): User? {
        return withContext(Dispatchers.IO) {
            repo.findUserByUID(uid)
        }
    }

    /**
     * Método que sirve para encontrar un usuario mediante su nombre de usuario
     * @param username Nombre de usuario del usuario a buscar
     * @return El usuario encontrado, si no existe será nulo
     * */
    suspend fun findUserByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            repo.findUserByUserName(username)
        }
    }

    /**
     * Método que sirve para borrar un usuario
     * @param uid UID del usuario a borrar de la base de datos
     * @param friends Lista de amigos del usuario, usado para eliminar de estos la referencia del usuario que
     * se borra
     * @param groups Lista de grupos a los que pertenece el usuario, usado para eliminar de estos la referencia del usuario que
     * se borrar
     * @return El resultado de borrar el usuario
     * */
    suspend fun deleteUser(
        uid: String,
        friends: List<ListItemUiModel>,
        groups: List<ListItemUiModel.Group>
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val result = repo.deleteUser(uid, friends, groups)
            if(result) {
                for (group in groups){
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(group.uid)
                }
            }
            return@withContext result
        }
    }

    /**
     * Método que sirve para cambiar el nombre de usuario del usuario
     * @param uid UID del usuario del cual se cambiará el nombre de usuario
     * @param newUsername Nuevo nombre de usuario
     * @return La tarea de cambiar el nombre de usuario
     * */
    fun updateUsername(uid: String, newUsername: String): Task<Void> {
        return repo.updateUsername(uid, newUsername)
    }

    /**
     * Método que sirve para eliminar la foto de perfil del usuario actual
     * @param path Ruta de la foto de perfil del usuario
     * @param currentUserUid UID del usuario actual
     * @param onComplete Función que se llama al terminar de borrar la foto de perfil
     * */
    fun deleteProfilePic(
        path: String,
        currentUserUid: String,
        onComplete: (task: Task<Void>) -> Unit
    ) {
        storageRepository.deletePhoto(path).addOnCompleteListener {
            if (it.isSuccessful) {
                repo.deleteProfilePic(currentUserUid).addOnCompleteListener { task ->
                    onComplete(task)
                }
            } else {
                onComplete(it)
            }
        }
    }

    /**
     * Método que sirve para subir una foto de perfil a partir de un Uri
     * @param prefix Prefijo usado para identificar desde donde se carga la foto
     * @param uri Uri de la foto que se cargar
     * @param currentUserUid UID del usuario actual
     * @param onCompleteListener Función que se llama al terminar de subir la foto de perfil
     * */
    fun uploadProfilePicByUri(
        prefix: String,
        uri: Uri,
        currentUserUid: String,
        onCompleteListener: (Task<Void>, path: String) -> Unit
    ) {
        storageRepository.saveImageFromUri(uri, currentUserUid).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.metadata?.let { it1 ->
                    repo.setProfilePic(prefix + it1.path, currentUserUid)
                        .addOnCompleteListener { task ->
                            onCompleteListener(
                                task,
                                prefix + it1.path
                            )
                        }
                }
            }
        }
    }

    /**
     * Método que sirve para subir una foto de perfil a partir de un Bitmap
     * @param prefix Prefijo usado para identificar desde donde se carga la foto
     * @param bitmap Bitmap de la foto que se cargar
     * @param currentUserUid UID del usuario actual
     * @param onCompleteListener Función que se llama al terminar de subir la foto de perfil
     * */
    fun uploadProfilePicByBitmap(
        prefix: String,
        bitmap: Bitmap,
        currentUserUid: String,
        onCompleteListener: (Task<Void>, path: String) -> Unit
    ) {
        storageRepository.saveImageFromBitmap(bitmap, currentUserUid).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.metadata?.let { it1 ->
                    repo.setProfilePic(prefix + it1.path, currentUserUid)
                        .addOnCompleteListener { task ->
                            onCompleteListener(
                                task,
                                prefix + it1.path
                            )
                        }
                }
            }
        }
    }

    /**
     * Método que sirve para cargar la foto de perfil del usuario actual
     * @param context Contexto usado para cargar la foto en la vista
     * @param path Ruta de la foto de perfil
     * @param view Vista en donde cargar la foto de perfil
     * */
    fun loadProfilePic(context: Context, path: String, view: ImageView) {
        val imageLoader = ListItemImageLoader(context)
        imageLoader.loadImage(path, view)
    }
}