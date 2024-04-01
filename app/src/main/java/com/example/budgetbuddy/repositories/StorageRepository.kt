package com.example.budgetbuddy.repositories

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.UploadTask.TaskSnapshot
import java.io.ByteArrayOutputStream
import java.util.UUID

class StorageRepository {
    private val reference = FirebaseStorage.getInstance().reference

    fun saveImageFromUri(uri: Uri, path: String):UploadTask{
       return reference.child("images/$path").putFile(uri)
    }

    fun saveImageFromBitmap(bitmap: Bitmap, path: String):UploadTask{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        return reference.child("images/$path").putBytes(data)
    }

    fun getImageUriFromPath(path:String):Task<Uri>{
        return reference.child(path).downloadUrl
    }
}