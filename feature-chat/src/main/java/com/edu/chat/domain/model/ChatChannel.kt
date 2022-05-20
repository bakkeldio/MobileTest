package com.edu.chat.domain.model

data class ChatChannel(
    val channelId: String,
    val role: String
){
    constructor(): this("", "")
}