package com.example.budgetbuddy.model

/**
 * Clase que contiene los atributos necesarios para representar un mensaje.
 * @param text Texto del mensaje.
 * @param senderUID UID del usuario que ha enviado el mensaje.
 * @param sentDate Fecha del servidor en la cual se envió el mensaje
 * @param type Tipo del mensaje, puede ser [MESSAGE_TYPE.TEXT] o [MESSAGE_TYPE.IMAGE]
 * @param imgPath Ruta de la imagen adjunto en el caso de que el mensaje sea de tipo [MESSAGE_TYPE.IMAGE]
 *
 * @author Armando Guzmán
 * */
data class Message(
    val text: String? = null,
    val senderUID: String? = null,
    val sentDate: Long? = null,
    val type: MESSAGE_TYPE? = null,
    val imgPath: String? = null
)