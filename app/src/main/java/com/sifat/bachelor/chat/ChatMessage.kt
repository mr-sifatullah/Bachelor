package com.sifat.bachelor.chat

data class ChatMessage(
    val senderId: String = "",
    val senderName: String = "",
    val message: String = "",
    val time: String = "",
    val seenStatus: Boolean = false,
    val timestamp: Any? = null
)

