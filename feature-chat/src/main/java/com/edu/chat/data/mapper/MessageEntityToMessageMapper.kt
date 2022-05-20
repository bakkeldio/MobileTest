package com.edu.chat.data.mapper

import com.edu.chat.data.db.entity.MessageEntity
import com.edu.chat.domain.model.Message

object MessageEntityToMessageMapper {

    fun map(messageEntity: MessageEntity) =
        Message(
            messageEntity.uid,
            messageEntity.time,
            messageEntity.messageType,
            0,
            null,
            null,
            messageEntity.messageUri,
            null,
            messageEntity.fileName,
            messageEntity.fileExtension,
            messageEntity.fileSize,
            messageEntity.chatUsers,
            messageEntity.to,
            messageEntity.from,
            messageEntity.senderName,
            messageEntity.recipientRole
        )
}