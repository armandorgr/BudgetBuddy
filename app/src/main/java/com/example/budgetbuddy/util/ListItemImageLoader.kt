package com.example.budgetbuddy.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.budgetbuddy.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

/**
 * Clase de utilidad que sirve para cargar imagenes de items cargados en RecyclerViews
 * @param context Contexto usado por glide para cargar la imagen
 * */
class ListItemImageLoader(
    private val context: Context
) {
    /**
     * Método que sirve para cargar una imagen de un item cargado en un RecyclerView
     * @param path Ruta de la imagen dentro de Firebase Storage
     * @param view ImageView en donde se cargará la imagen
     * @param onComplete Función que se llama al completar la carga de la foto
     * */
    fun loadImage(path: String, view: ImageView, onComplete: ((uri: Uri) -> Unit)? = null) {
        val prefix = path.substring(0, 2)
        val url = path.substring(2)
        if (prefix == Utilities.PROFILE_PIC_ST) {
            val storageReference = Firebase.storage.getReference(url)
            Log.d("foto", url)
            storageReference.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    val uri = it.result as Uri
                    if (onComplete != null) onComplete(uri)
                    Glide.with(context).load(uri).placeholder(R.drawable.default_profile_pic)
                        .into(view)
                } else {
                    Log.d("prueba", "error: ${it.exception?.message}")
                }
            }
        } else {
            Glide.with(context).load(url).placeholder(R.drawable.default_profile_pic).into(view)
        }
    }
}