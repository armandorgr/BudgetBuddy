package com.example.budgetbuddy.util

import android.app.AlertDialog
import android.widget.ArrayAdapter

/**
 * Clase que contiene los datos necesarios para pintar una ventana emergente simple
 * @param title Título de la ventana emergente
 * @param text Mensaje a mostrar dentro de la ventana emergente
 * @param btnText Texto mostrado en el boton de la ventana emergente
 * @param onDismiss Función que se llama al quitar la ventana emergente
 * */
data class Result(
    val title: String,
    val text: String,
    val btnText: String,
    val onDismiss: () -> Unit
)

/**
 * Clase que contiene los datos necesarios para pintar una ventana emergente con un campo para
 * introducir texto
 * @param title Título de la ventana emergente
 * @param hint Texto a usar como pista dentro del campo de texto
 * @param onOk Función llamada al pulsar sobre el botón de ok
 * @param onDismiss Función que se llama al quitar la ventana emergente
 * */
data class PromptResult(
    val title: String,
    val hint: String,
    val onOk: (dialog: AlertDialog) -> Unit,
    val onDismiss: () -> Unit
)

/**
 * Clase que contiene los datos necesarios para pintar una ventana emergente con un DatePicker
 * para seleccionar una fecha
 * @param title Título de la ventana emergente
 * @param onOk Función llamada al pulsar sobre el botón de ok
 * @param onDismiss Función que se llama al quitar la ventana emergente
 * */
data class DateResult(
    val title: String,
    val onOk: (dialog: AlertDialog) -> Unit,
    val onDismiss: () -> Unit
)

/**
 * Clase que contiene los datos necesarios para pintar una ventana emergente con un botón
 * de ok y otro de cancelar
 * @param title Título de la ventana emergente
 * @param text Mensaje a mostrar dentro de la ventana emergente
 * @param onOk Función llamada al pulsar sobre el botón de ok
 * @param onDismiss Función que se llama al quitar la ventana emergente
 * */
data class ResultOkCancel(
    val title: String,
    val text: String,
    val onOk: (dialog: AlertDialog) -> Unit,
    val onDismiss: () -> Unit
)

/**
 * Clase que contiene los datos necesarios para pintar una ventana emergente con dos
 * campos de texto para introducir datos por el usuario
 * @param title Título de la ventana emergente
 * @param hint1 Texto usado como pista para el primer campo
 * @param hint2 Text usado como pista para el segundo campo
 * @param onOk Función llamada al pulsar sobre el botón de ok
 * @param onDismiss Función que se llama al quitar la ventana emergente
 * */
data class TwoPromptResult(
    val title: String,
    val hint1: String,
    val hint2: String,
    val onOk: (dialog: AlertDialog) -> Unit,
    val onDismiss: () -> Unit
)

/**
 * Clase que contiene los datos necesarios para pintar una ventana emergente con un Picker
 * con una seria de datos para seleccionar por el usuario
 * @param title Título de la ventana emergente
 * @param adapter Adaptador usado para poblar el Picker
 * @param onOk Función llamada al pulsar sobre el botón de ok pasando por argumento
 * la selección del usuario
 * @param onDismiss Función que se llama al quitar la ventana emergente
 * */
data class PickerData(
    val title: String,
    val adapter: ArrayAdapter<String>,
    val onOk: (dialog: AlertDialog, selection: String) -> Unit,
    val onDismiss: () -> Unit
)
