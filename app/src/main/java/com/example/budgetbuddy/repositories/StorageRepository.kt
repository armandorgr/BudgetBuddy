package com.example.budgetbuddy.repositories

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

/**
 * Clase repositorio de las imagenes de la aplicación, esta clase contiene todos los métodos necesarios
 * para realizar operaciones de subida y eliminación de fotos dentro Firebase Storage
 *
 * La forma de trabajar con Storage fue consultada en la documentación de Firebase Storage :
 * https://firebase.google.com/docs/storage/android/upload-files?hl=es
 * https://firebase.google.com/docs/storage/android/download-files?hl=es
 * @author Armando Guzmán
 * */
class StorageRepository {
    private val reference = FirebaseStorage.getInstance().reference

    /**
     * Método que sirve para subir una imagen a partir de un uri
     * @param uri Uri de la imagen a subir
     * @param path Ruta con la cual se guardará la imagen
     * @return Tarea de subir la foto al Storage
     * */
    fun saveImageFromUri(uri: Uri, path: String): UploadTask {
        return reference.child("images/$path").putFile(uri)
    }

    /**
     * Método que sirve para subir una imagen a partir de un bitmap
     * @param bitmap Bitmap de la imagen a subir
     * @param path Ruta con la cual se guardará la imagen
     * @return Tarea de subir la foto al Storage
     * */
    fun saveImageFromBitmap(bitmap: Bitmap, path: String): UploadTask {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        return reference.child("images/$path").putBytes(data)
    }

    /**
     * Método que sirve para eliminar una imagen a partir de su ruta
     * @param path Ruta de la imagen dentro del Storage
     * @return La tarea de eliminar la imagen
     * */
    fun deletePhoto(path: String): Task<Void> {
        return reference.child(path).delete()
    }

}