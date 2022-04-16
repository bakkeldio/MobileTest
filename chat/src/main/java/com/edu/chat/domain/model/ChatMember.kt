package com.edu.chat.domain.model


data class ChatMember(
    val uid: String,
    val name: String,
    val avatarUrl: String?
){
    constructor(): this("", "", "")
}