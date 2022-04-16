package com.edu.chat.domain.usecase

import com.edu.chat.domain.model.Message
import com.edu.chat.domain.repository.IChatRepo
import com.edu.common.data.Result
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class MarkNewMessagesAsSeenUseCase @Inject constructor(private val chatRepo: IChatRepo) {


    suspend operator fun invoke(otherUserId: String, channelId: String, messages: List<Message>): Result<Unit> {
        val messagesToMark = messages.filter {
            it.status == 1 && otherUserId == it.from
        }.map {
            it.uid
        }
        return chatRepo.markNewMessagesAsSeen(channelId, messagesToMark)
    }

}