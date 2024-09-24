package com.sifat.bachelor.api.model

data class NotificationData(
    val message: Message // Message object containing the topic and notification
)

data class Message(
    val topic: String, // Topic to send the notification to
    val notification: Notifications, // Notification object
    val data: Map<String, String> // Additional data
)

data class Notifications(
    val title: String, // Title of the notification
    val body: String   // Body of the notification
)


