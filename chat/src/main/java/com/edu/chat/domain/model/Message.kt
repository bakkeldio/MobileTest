package com.edu.chat.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Message(
    val uid: String,
    @ServerTimestamp
    val time: Date,
    val messageType: String,
    val status: Int,
    val message: String,
    val chatUsers: List<String>,
    val to: String,
    val from: String,
    val senderName: String,
    val recipientRole: String
) {
    constructor() : this("", Date(), "", 0, "", emptyList(), "", "", "", "")
}