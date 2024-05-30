package com.example.budgetbuddy.model

/**
 * Clase que sirve para contener los datos necesarios para representar una invitación
 * @param pic Ruta de la foto del usuario o grupo que ha enviado la invitación
 * @param senderUid UID del usuario o grupo que ha enviado la invitación
 * @param senderName Nombre del usuario o grupo que ha enviada la invitación
 * @param type Tipo de la invitación
 * @param dateSent Fecha del servidor en la cual fue enviada la invitación
 *
 * @author Armando Guzmám
 * */
data class InvitationUiModel(
    val pic:String? = null,
    val senderUid: String? = null,
    val senderName: String? = null,
    val type: INVITATION_TYPE? = null,
    var dateSent: Any? = null
)