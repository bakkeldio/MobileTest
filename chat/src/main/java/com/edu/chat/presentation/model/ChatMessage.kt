package com.edu.chat.presentation.model

import com.edu.chat.domain.model.Message

sealed class ChatMessage(val uid: String, val message: Message) {
    class ReceivedMessage(messageUid: String, msg: Message) :
        ChatMessage(messageUid, msg)

    class SentMessage(messageUid: String, msg: Message) :
        ChatMessage(messageUid, msg)

    class Header(val date: String) : ChatMessage("", Message())
}