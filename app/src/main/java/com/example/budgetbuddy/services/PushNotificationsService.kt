package com.example.budgetbuddy.services

import com.example.budgetbuddy.activities.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService

/**
 * Clase de servicio encarga de escuchar por nuevas notificaciones y responder a las que
 * se reciban
 *
 * Consultado de aquí: https://www.youtube.com/watch?v=q6TL2RyysV4
 *
 * @author Armando Guzmán
 * */
class PushNotificationsService : FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


}