package com.edu.chat.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edu.chat.data.db.Converters
import kotlinx.serialization.Serializable
import java.util.*

@Entity(tableName = "message")
class MessageEntity(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "message_type") val messageType: String,
    @ColumnInfo(name = "message_uri") val messageUri: String,
    @ColumnInfo(name = "chat_users") val chatUsers: List<String>,
    val to: String,
    val from: String,
    val senderName: String,
    val recipientRole: String,
    val time: Date,
    val channelId: String,
    val fileName: String,
    val fileExtension: String,
    val fileSize: String
)