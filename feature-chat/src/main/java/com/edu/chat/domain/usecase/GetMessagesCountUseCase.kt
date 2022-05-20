package com.edu.chat.domain.usecase

import com.edu.chat.domain.repository.IChatRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetMessagesCountUseCase @Inject constructor(private val chatRepo: IChatRepo) {

    suspend operator fun invoke(channelId: String) = chatRepo.getMessagesCountInChannel(channelId)
}