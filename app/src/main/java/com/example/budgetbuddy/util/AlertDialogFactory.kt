package com.example.budgetbuddy.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.budgetbuddy.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Clase de utilidad que contiene métodos que sirven para crear ventanas emergentes dentro de la aplicación
 * La forma de crear los AlertDialog fue consultada aquí: https://developer.android.com/develop/ui/views/components/dialogs
 * @param context Contexto usado para acceder a los recursos de la aplicación
 *
 * @author Armando Guzmán
 * */
class AlertDialogFactory(private val context: Context) {

    /**
     * Método que sirve para crear una ventana emergente simple, usada para mostrar ventanas de éxito o fallo
     * @param layout Layout a mostrar
     * @param view Vista sobre la cual montar la ventana emergente
     * @param result Objeto con los datos usados para pintar la ventana emergente
     * @return La vista del AlertDialog
     * */
    fun createDialog(layout: Int, view: View, result: Result): View {
        val constraintLayout =
            view.findViewById<ConstraintLayout>(R.id.successConstraintLayout)
        val dialogView =
            LayoutInflater.from(context).inflate(layout, constraintLayout)
        val btn = dialogView.findViewById<Button>(R.id.dialog_button)
        dialogView.findViewById<TextView>(R.id.alertDialogTitle).text = result.title
        dialogView.findViewById<TextView>(R.id.alertDialogText).text = result.text
        btn.text = result.btnText

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()

        alertDialog.setOnDismissListener {
            result.onDismiss()
        }

        btn.setOnClickListener {
            alertDialog.dismiss()
        }
        return dialogView
    }


    /**
     * Método que sirve para crear una ventana emergente para seleccionar una fecha.
     * @param view Vista sobre la cual montar la ventana emergente
     * @param result Objeto con los datos usados para pintar la ventana emergente
     * @return La vista del AlertDialog
     * */
    fun createDatePickerDialog(view: View, result: DateResult): View {
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.dateDialogConstraintLayout)
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.custom_date_prompt_dialog, constraintLayout)
        val btnOk = dialogView.findViewById<Button>(R.id.dialog_button_ok)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_button_cancel)
        val title = dialogView.findViewById<TextView>(R.id.alertDialogTitle)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
        title.text = result.title

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()


        alertDialog.show()

        alertDialog.setOnDismissListener {
            result.onDismiss()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        btnOk.setOnClickListener {
            result.onOk(alertDialog)
            alertDialog.dismiss()
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        return dialogView
    }

    /**
     * Método que sirve para crear una ventana emergente con un editText para que el usuario introduzca datos.
     * @param view Vista sobre la cual montar la ventana emergente
     * @param result Objeto con los datos usados para pintar la ventana emergente
     * @param isPassword Valor que indica si el EditText mostrado debe tener formato de contraseña o no, por defecto es false
     * @return La vista del AlertDialog
     * */
    fun createPromptDialog(view: View, result: PromptResult, isPassword: Boolean = false): View {
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.promptConstraintLayout)
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.custom_prompt_dialog, constraintLayout)
        val btnOk = dialogView.findViewById<Button>(R.id.dialog_button_ok)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_button_cancel)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.newEditText)
        val textLayout = dialogView.findViewById<TextInputLayout>(R.id.promptTextLayout)
        val builder = AlertDialog.Builder(context)

        textLayout.hint = result.hint
        dialogView.findViewById<TextView>(R.id.alertDialogTitle).text = result.title

        if (isPassword) {
            editText.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
            textLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            Log.d("prueba", "${editText.inputType}")
        }
        builder.setView(dialogView)
        val alertDialog = builder.create()
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()


        alertDialog.setOnDismissListener {
            result.onDismiss()
        }
        btnOk.setOnClickListener {
            result.onOk(alertDialog)
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        return dialogView
    }

    /**
     * Método que sirve para crear una ventana emergente con un editText para que el usuario introduzca datos.
     * @param view Vista sobre la cual montar la ventana emergente
     * @param result Objeto con los datos usados para pintar la ventana emergente
     * @param isEmailPassword Valor que indica si los EditText mostrados deben tener formato de email y contraseña o no, por defecto es false
     * @return La vista del AlertDialog
     * */
    fun createTwoPromptLayout(
        view: View,
        result: TwoPromptResult,
        isEmailPassword: Boolean = false
    ): View {
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.twoPromptsDialog)
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.custom_two_prompts_dialog, constraintLayout)
        val btnOk = dialogView.findViewById<Button>(R.id.dialog_button_ok)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_button_cancel)

        dialogView.findViewById<TextView>(R.id.alertDialogTitle).text = result.title
        dialogView.findViewById<TextInputLayout>(R.id.emailTextLayout).hint = result.hint1
        val passLayOut = dialogView.findViewById<TextInputLayout>(R.id.passwordTextLayout)
        val passET = dialogView.findViewById<TextInputEditText>(R.id.passwordEditText)
        passLayOut.hint = result.hint2

        if (!isEmailPassword) {
            passLayOut.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
            passET.inputType = EditorInfo.TYPE_CLASS_TEXT
        }

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()

        alertDialog.setOnDismissListener {
            result.onDismiss()
        }

        btnOk.setOnClickListener {
            result.onOk(alertDialog)
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        return dialogView
    }

    /**
     * Método que sirve para crear una ventana emergente con un boton de ok y otro de cancelar
     * @param view Vista sobre la cual montar la ventana emergente
     * @param result Objeto con los datos usados para pintar la ventana emergente
     * @return La vista del AlertDialog
     * */
    fun createOkCancelDialog(view: View, result: ResultOkCancel): View {
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.okCancelConstraintLayout)
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.custom_ok_cancel_dialog, constraintLayout)
        val btnOk = dialogView.findViewById<Button>(R.id.dialog_button_ok)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_button_cancel)

        dialogView.findViewById<TextView>(R.id.alertDialogTitle).text = result.title
        dialogView.findViewById<TextView>(R.id.alertDialogText).text = result.text

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()

        alertDialog.setOnDismissListener {
            result.onDismiss()
        }

        btnOk.setOnClickListener {
            result.onOk(alertDialog)
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        return dialogView
    }

    /**
     * Método que sirve para crear una ventana emergente con las opciones para cargar una foto;
     * desde la galería o cámara
     * @param view Vista sobre la cual montar la ventana emergente
     * @param onGallery Función que se llama al escoger cargar la foto desde la galería
     * @param onCamera Función que se llama al escoger cargar la foto desde la cámara
     * @param onDelete Función que se llama al pulsar sobre la opcion de borrar la foto, por defecto es null
     * @return La vista del AlertDialog
     * */
    fun createPhotoDialog(
        view: View,
        onGallery: () -> Unit,
        onCamera: () -> Unit,
        onDelete: (() -> Unit)? = null
    ): View {
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.photoConstraintLayout)
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.custom_photo_dialog, constraintLayout)
        val btnGallery = dialogView.findViewById<MaterialButton>(R.id.dialog_button_galley)
        val btnCamera = dialogView.findViewById<MaterialButton>(R.id.dialog_button_camera)
        val btnDelete = dialogView.findViewById<MaterialButton>(R.id.dialog_button_delete)


        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        btnGallery.setOnClickListener {
            onGallery()
            alertDialog.dismiss()
        }
        btnCamera.setOnClickListener {
            onCamera()
            alertDialog.dismiss()
        }
        onDelete?.let {
            btnDelete.visibility = View.VISIBLE
            btnDelete.setOnClickListener {
                onDelete()
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
        return dialogView
    }

    /**
     * Método que sirve para crear una ventana emergente con un picker que despliega una seria de opciones a escoger por el usuario
     * @param view Vista sobre la cual montar la ventana emergente
     * @param data Objeto que contiene los datos usados para pintar la ventana emergente
     * @return La vista del AlertDialog
     * */
    fun createPickerDialog(view: View, data: PickerData): View {
        val constraintLayout =
            view.findViewById<ConstraintLayout>(R.id.spinner_dialog_constraintLayout)
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.custom_spinner_dialog, constraintLayout)

        val btnOk = dialogView.findViewById<Button>(R.id.dialog_button_ok)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_button_cancel)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinner)

        spinner.adapter = data.adapter
        dialogView.findViewById<TextView>(R.id.alertDialogTitle).text = data.title

        val builder = AlertDialog.Builder(context)

        builder.setView(dialogView)
        val alertDialog = builder.create()
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()


        alertDialog.setOnDismissListener {
            data.onDismiss()
        }
        btnOk.setOnClickListener {
            data.onOk(alertDialog, spinner.selectedItem as String)
        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        return dialogView
    }

    /**
     * Método que sirve para crear una ventana emergente en la cual se muestra una imagen cargada en pantalla completa
     * @param view Vista sobre la cual montar la ventana emergente
     * @param path Ruta de la imagen a cargar
     * @param imageLoader Cargador de las imagenes usado para cargar la immagen
     * @return La vista del AlertDialog
     * */
    fun createFullScreenPhotoDialog(view: View, path: String, imageLoader: ListItemImageLoader) {
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.fullImageConstraintLayout)
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.full_image_layout, constraintLayout)
        val btnBack = dialogView.findViewById<ImageView>(R.id.backButton)
        val imageView = dialogView.findViewById<ImageView>(R.id.imageView)


        imageLoader.loadImage(path, imageView)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.show()

        btnBack.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}