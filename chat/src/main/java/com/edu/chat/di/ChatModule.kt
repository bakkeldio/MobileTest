package com.edu.chat.di

import com.edu.chat.data.ChatRepository
import com.edu.chat.domain.repository.IChatRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent


@Module
@InstallIn(ActivityRetainedComponent::class)
interface ChatModule {

    @Binds
    fun bindChatRepository(repo: ChatRepository): IChatRepo
}