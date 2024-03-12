package com.example.budgetbuddy.util

import android.app.AlertDialog
import android.view.View
import com.google.android.material.textfield.TextInputLayout
import org.junit.matchers.JUnitMatchers

data class Result(
    val title: String,
    val text: String,
    val btnText: String,
    val onDismiss: () -> Unit
)

data class PromptResult(
    val title: String,
    val hint: String,
    val onOk: (dialog:AlertDialog) -> Unit,
    val onDismiss: () -> Unit
)

data class DateResult(
    val title: String,
    val onOk:(dialog:AlertDialog) -> Unit,
    val onDismiss: () -> Unit
)

data class ResultOkCancel(
    val title: String,
    val text:String,
    val onOk:(dialog:AlertDialog) -> Unit,
    val onDismiss: () -> Unit
)

data class TwoPromptResult(
    val title: String,
    val hint1:String,
    val hint2:String,
    val onOk:(dialog:AlertDialog) -> Unit,
    val onDismiss: () -> Unit
)
