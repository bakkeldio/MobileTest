package com.edu.chat.domain.usecase

import com.edu.chat.domain.repository.IChatRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetOrCreateChannelIdUseCase @Inject constructor(private val chatRepo: IChatRepo) {

    suspend operator fun invoke(otherUserId: String) = chatRepo.getOrCreateChannel(otherUserId)
}