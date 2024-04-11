package com.example.budgetbuddy.util

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class ImageLoader (fragment: Fragment,onSuccessGalley: (Uri)->Unit,onSuccessCamera: (Bitmap) -> Unit,onFailure: ()->Unit ){
    private val galleryActivityLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            if(it.data!=null){
                val img = it.data!!.data
                img?.let(onSuccessGalley)
            }
        }else{
           onFailure()
        }
    }
    private val cameraActivityLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            val img: Bitmap = it.data?.extras?.get("data") as Bitmap
            onSuccessCamera(img)
        }else{
            onFailure()
        }
    }

    fun getPhotoFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityLauncher.launch(intent)
    }

    fun getPhotoFromCamera(){
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityLauncher.launch(intent)
    }
}