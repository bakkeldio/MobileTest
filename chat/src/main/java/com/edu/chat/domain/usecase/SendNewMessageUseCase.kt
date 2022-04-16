package com.edu.chat.domain.usecase

import com.edu.chat.domain.model.ChatChannel
import com.edu.chat.domain.model.MessageTypeEnum
import com.edu.chat.domain.repository.IChatRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@ViewModelScoped
class SendNewMessageUseCase @Inject constructor(private val chatRepo: IChatRepo) {

    suspend operator fun invoke(
        uid: String,
        message: String,
        messageType: MessageTypeEnum,
        userId: String,
        channel: ChatChannel
    ) = chatRepo.sendNewMessageToUser(channel, userId, uid, message, messageType)
}