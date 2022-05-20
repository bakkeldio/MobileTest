package com.edu.chat.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ChannelItem(
    val userIds: List<String> = emptyList(),
    val latestMessage: String? = null,
    val latestMessageStatus: Int = 0,
    @ServerTimestamp
    val latestMessageTime: Date? = null,
    val latestMessageType: String? = null,
    val latestMessageUid: String? = null,
    val senderId: String? = null,
    val newMessages: Int = 0
)