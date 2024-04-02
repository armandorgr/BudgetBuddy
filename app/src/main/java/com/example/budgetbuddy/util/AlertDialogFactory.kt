package com.example.budgetbuddy.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.budgetbuddy.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.EndIconMode

class AlertDialogFactory(private val context: Context) {
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


    @SuppressLint("MissingInflatedId")
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

    @SuppressLint("MissingInflatedId")
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
            btnDelete.setOnClickListener{
                onDelete()
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
        return dialogView
    }
}