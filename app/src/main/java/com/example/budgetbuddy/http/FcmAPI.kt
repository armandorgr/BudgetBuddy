package com.example.budgetbuddy.http

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz la cual define las peticiones HTTP necesarias para comunicarse con el backend local
 * el cual enviará las respáctivas peticiones a Firebase para enviar las notificaciones
 * El modo de trabajar con el backend local fue consultado aquí: https://www.youtube.com/watch?v=q6TL2RyysV4
 *
 * @author Armando Guzmán
 * */
interface FcmAPI {

    /**
     * Método que sirve para enviar un petición POST a la ruta /send del backend local,
     * con lo cual se enviará el mensaje pasado por argumento a los aquellos dispositivos suscritos al topico
     * @param body Objeto contenedor de la información necesaria para enviar el mensaje
     * @return La llamada a la petición
     * */
    @POST("/send")
    fun sendMessage(
        @Body body: SendMessageRequest
    ):Call<Void>

    /**
     * Método que sirve para enviar un petición POST a la ruta /subscribe del backend local,
     * con lo cual se suscribirá el dispositivo identificado por el token [SubscribeRequest.token] al topico [SubscribeRequest.topic]
     * @param body Objeto contenedor de la información necesaria para realizar la suscripción
     * @return La llamada a la petición
     * */
    @POST("/subscribe")
    fun subscribe(
        @Body body: SubscribeRequest
    ):Call<Void>

    /**
     * Método que sirve para enviar un petición POST a la ruta /unsubscribe del backend local,
     * con lo cual se desuscribirá el dispositivo identificado por el token [SubscribeRequest.token] del topico [SubscribeRequest.topic]
     * @param body Objeto contenedor de la información necesaria para realizar la desuscripción
     * @return La llamada a la petición
     * */
    @POST("/unsubscribe")
    fun unsubscribe(
        @Body body: UnsubscribeRequest
    ):Call<Void>
}