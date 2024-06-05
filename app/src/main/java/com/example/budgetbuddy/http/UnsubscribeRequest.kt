package com.example.budgetbuddy.http

// Forma de trabajar con las notificaciones consultdo aquí: https://www.youtube.com/watch?v=q6TL2RyysV4
/**
 * Objeto que sirve para contener los datos necesarios para que un dispósitivo identificado por [token] se desuscriba
 * a la notificaciones enviadas al [topic].
 *
 * @author Armando Guzmán
 * */
data class UnsubscribeRequest(
    val tokens: MutableList<String>,
    val topic: String
)

