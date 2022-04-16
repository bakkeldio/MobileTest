package com.edu.chat.domain.model

data class MessageComposed(
    val removedMessages: List<Message>,
    val modifiedMessages: List<Message>,
    val addedMessages: List<Message>
)