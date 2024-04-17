package com.example.budgetbuddy.util

import android.app.AlertDialog
import android.widget.ArrayAdapter

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

data class PickerData(
    val title:String,
    val adapter: ArrayAdapter<CharSequence>,
    val onOk: (dialog: AlertDialog, selection: String)->Unit,
    val onDismiss: () -> Unit
)
