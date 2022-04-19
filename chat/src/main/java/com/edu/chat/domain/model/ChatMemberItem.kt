package com.edu.chat.domain.model

import java.util.*

data class ChatMemberItem(
    val lastMessageType: MessageTypeEnum,
    val chatUserUid: String,
    val chatUserName: String,
    val chatUserAvatar: String?,
    val messageAuthor: MessageAuthorEnum,
    val lastMessageStatus: Int,
    val time: Date?,
    val message: String?,
    val newMessagesCount: Int
)