package com.edu.chat.presentation.model

import com.edu.chat.domain.model.Message

sealed class ChatMessage(
    val uid: String, val message: Message
) {
    data class ReceivedMessage(
        val messageUid: String,
        val msg: Message,
        val imageIsDownloaded: Boolean = false
    ) :
        ChatMessage(messageUid, msg)

    data class SentMessage(
        val messageUid: String,
        val msg: Message,
        val showRetry: Boolean = false,
        val imageIsUploaded: Boolean = false
    ) :
        ChatMessage(messageUid, msg)

    class Header(val date: String) : ChatMessage("", Message())
}