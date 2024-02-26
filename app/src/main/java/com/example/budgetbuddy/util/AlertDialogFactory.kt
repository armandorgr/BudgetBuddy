package com.example.budgetbuddy.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.budgetbuddy.R

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
        btn.setOnClickListener{
            result.onClick()
            alertDialog.dismiss()
        }
        return dialogView
    }
}