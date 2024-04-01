package com.example.budgetbuddy.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.budgetbuddy.model.User
import com.example.budgetbuddy.repositories.StorageRepository
import com.example.budgetbuddy.repositories.UsersRepository
import com.example.budgetbuddy.util.ListItemImageLoader
import com.example.budgetbuddy.validations.validators.EmailValidator
import com.example.budgetbuddy.validations.validators.PasswordValidator
import com.example.budgetbuddy.validations.validators.UsernameValidator
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val storageRepository: StorageRepository
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

    fun loadProfilePic(context: Context, path:String, view:ImageView) {
        val imageLoader = ListItemImageLoader(context)
        imageLoader.loadImage(path, view)
    }
}