package com.example.budgetbuddy.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.budgetbuddy.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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

        alertDialog.setOnDismissListener{
            result.onDismiss()
        }

        btn.setOnClickListener{
            alertDialog.dismiss()
        }
        return dialogView
    }


    fun createPromptDialog(view:View, result: PromptResult):View{
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.promptConstraintLayout)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_prompt_dialog, constraintLayout)
        val btnOk = dialogView.findViewById<Button>(R.id.dialog_button_ok)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_button_cancel)
        val editText = dialogView.findViewById<EditText>(R.id.newEmailEditText)
        val textLayout = dialogView.findViewById<TextInputLayout>(R.id.promptTextLayout)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        textLayout.hint = result.hint
        dialogView.findViewById<TextView>(R.id.alertDialogTitle).text = result.title
        val alertDialog = builder.create()

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()


        alertDialog.setOnDismissListener{
            result.onDismiss()
        }
        btnOk.setOnClickListener{
            result.onOk(alertDialog)
        }
        btnCancel.setOnClickListener{
            alertDialog.dismiss()
        }
        return dialogView
    }


    fun createTwoPromptLayout(view: View, result: TwoPromptResult):View{
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.twoPromptsDialog)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_two_prompts_dialog, constraintLayout)
        val btnOk = dialogView.findViewById<Button>(R.id.dialog_button_ok)
        val btnCancel = dialogView.findViewById<Button>(R.id.dialog_button_cancel)
        val editText1 = dialogView.findViewById<TextInputEditText>(R.id.EmailEditText)
        val editText2 = dialogView.findViewById<TextInputEditText>(R.id.passwordEditText)

        dialogView.findViewById<TextView>(R.id.alertDialogTitle).text = result.title
        dialogView.findViewById<TextInputLayout>(R.id.emailTextLayout).hint = result.hint1
        dialogView.findViewById<TextInputLayout>(R.id.passwordTextLayout).hint = result.hint2

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()

        alertDialog.setOnDismissListener{
            result.onDismiss()
        }

        btnOk.setOnClickListener{
            result.onOk(alertDialog)
        }
        btnCancel.setOnClickListener{
            alertDialog.dismiss()
        }

        return dialogView
    }
}