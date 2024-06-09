package com.budgetbuddy

import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification

@kotlinx.serialization.Serializable
data class SendMessageRequest(
    val to: String?,
    val notification: NotificationBody
)
@kotlinx.serialization.Serializable
data class NotificationBody(
    val title: String,
    val body: String
)

fun SendMessageRequest.toMessage(): Message{
    return Message.builder()
        .setNotification(
            Notification.builder()
                .setTitle(notification.title)
                .setBody(notification.body)
                .build()
        ).setTopic(to)
        .build()
}
