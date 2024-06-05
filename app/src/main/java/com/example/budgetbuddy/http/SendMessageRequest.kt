package com.example.budgetbuddy.http

// Forma de trabajar con las notificaciones consultdo aquí: https://www.youtube.com/watch?v=q6TL2RyysV4

/**
 * Objeto que sirve para contener los datos necesarios para mandar una notificación a Firebase y que
 * este la envía a los dispositivos identificados por el token
 * @param to Token del dispositivo o nombre del topico al cual enviar la notificación
 * @param notification Objeto contenedor de la información a mostrar en la notificación
 *
 * @author Armando Guzmán
 * */
data class SendMessageRequest(
    val to: String?,
    val notification: NotificationBody
)

/**
 * Objeto que sirve para contener los datos necesarios a mostrar dentro de una notificacion
 * @param title Título que tendra la notificación
 * @param body Mensaje que mostrará la notificación
 *
 * @author Armando Guzmán
 * */
data class NotificationBody(
    val title: String,
    val body: String
)
