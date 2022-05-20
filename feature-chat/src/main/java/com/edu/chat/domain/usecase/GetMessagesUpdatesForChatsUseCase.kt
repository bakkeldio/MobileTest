package com.edu.chat.domain.usecase

import com.edu.chat.domain.model.Message
import com.edu.chat.domain.repository.IChatRepo
import com.edu.common.data.Result
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetMessagesUpdatesForChatsUseCase @Inject constructor(private val chatRepo: IChatRepo) {

    operator fun invoke(): Flow<Result<List<Message>>> {
        return chatRepo.listenToNewMessagesFromChannels()

    }
}