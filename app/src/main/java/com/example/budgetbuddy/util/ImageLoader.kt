package com.example.budgetbuddy.util

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

/**
 * Clase de utlidad que contiene métodos para cargar imagenes desde la galería y la cámara del sistema
 * @param fragment Fragmento usado para registrar el Intent de abrir la galería o la cámara
 * @param onSuccessGalley Función que se llama al tener éxito al cargar la foto desde la galería
 * @param onSuccessCamera Función que se llama al tener éxito al cargar la foto desde al cámara
 * @param onFailure Función que se llama al fallar la carga de alguna manera
 *
 * @author Armmando Guzmán
 * */
class ImageLoader(
    fragment: Fragment,
    onSuccessGalley: (Uri) -> Unit,
    onSuccessCamera: (Bitmap) -> Unit,
    onFailure: () -> Unit
) {
    // Intent para abrir la galería
    private val galleryActivityLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    val img = it.data!!.data
                    img?.let(onSuccessGalley)
                }
            } else {
                onFailure()
            }
        }

    // Intent para abrir la cámara
    private val cameraActivityLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val img: Bitmap = it.data?.extras?.get("data") as Bitmap
                onSuccessCamera(img)
            } else {
                onFailure()
            }
        }

    /**
     * Método que sirve para abrir la galería y cargar una foto
     * */
    fun getPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityLauncher.launch(intent)
    }

    /**
     * Método que sirve para abrir la cámara y cargar una foto
     * */
    fun getPhotoFromCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityLauncher.launch(intent)
    }
}