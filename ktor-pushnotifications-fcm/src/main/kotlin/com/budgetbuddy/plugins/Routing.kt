package com.budgetbuddy.plugins

import com.budgetbuddy.sendNotification
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        sendNotification()
    }
}
