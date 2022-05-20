package com.edu.chat.domain.usecase

import com.edu.chat.domain.model.MessageComposed
import com.edu.chat.domain.repository.IChatRepo
import com.edu.common.data.Result
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ViewModelScoped
class GetNewChatMessagesUseCase @Inject constructor(private val chatRepo: IChatRepo) {

    suspend operator fun invoke(channelId: String): Flow<Result<MessageComposed>> {
        return chatRepo.getNewMessagesOfChat(channelId).catch {
            emit(Result.Error(it))
        }.flowOn(Dispatchers.IO)
    }
}