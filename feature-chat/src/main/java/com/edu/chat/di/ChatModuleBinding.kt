package com.edu.chat.di

import com.edu.chat.data.ChatRepository
import com.edu.chat.domain.repository.IChatRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface ChatModuleBinding {

    @Binds
    fun bindChatRepository(repo: ChatRepository): IChatRepo
}