package com.example.budgetbuddy.http

// Forma de trabajar con las notificaciones consultdo aquí: https://www.youtube.com/watch?v=q6TL2RyysV4
/**
 * Objeto que sirve para contener los datos necesarios para que un dispósitivo identificado por [token] se suscriba
 * a la notificaciones enviadas al [topic].
 *
 * @author Armando Guzmán
 * */
data class SubscribeRequest(
    val token: String,
    val topic: String
)