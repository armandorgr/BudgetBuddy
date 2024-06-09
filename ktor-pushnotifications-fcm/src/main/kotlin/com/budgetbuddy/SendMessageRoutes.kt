package com.budgetbuddy

import com.google.firebase.messaging.FirebaseMessaging
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.sendNotification(){
    route("/send"){
        post {
            val body = call.receiveNullable<SendMessageRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

                FirebaseMessaging.getInstance().sendAsync(body.toMessage())
                call.respond(HttpStatusCode.OK)

        }
    }
    route("/subscribe"){
        post {
            val body = call.receiveNullable<SubscribeRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            FirebaseMessaging.getInstance().subscribeToTopic(
                mutableListOf(body.token),
                body.topic
            )
            call.respond(HttpStatusCode.OK)
        }
    }
    route("/unsubscribe"){
        post {
            val body = call.receiveNullable<UnsubscribeRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            FirebaseMessaging.getInstance().unsubscribeFromTopic(
                body.tokens,
                body.topic
            )
            call.respond(HttpStatusCode.OK)
        }
    }
}