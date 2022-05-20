package com.edu.chat.domain.usecase

import com.edu.chat.domain.model.ChatChannel
import com.edu.chat.domain.model.MessageTypeEnum
import com.edu.chat.domain.repository.IChatRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@ViewModelScoped
class SendFileMessageUseCase @Inject constructor(private val chatRepo: IChatRepo) {

    suspend operator fun invoke(
        channel: ChatChannel,
        messageUid: String,
        userId: String,
        uri: String,
        messageTypeEnum: MessageTypeEnum
    ) = chatRepo.sendImageMessage(channel, userId, messageUid, uri, messageTypeEnum)
}