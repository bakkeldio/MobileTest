package com.edu.chat.data.di

import android.content.Context
import androidx.room.Room
import com.edu.chat.data.db.database.ChatDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.logging.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ChatModule {

    @Provides
    @Singleton
    fun provideMessageDao(chatDatabase: ChatDatabase) = chatDatabase.messageDao()

    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, ChatDatabase::class.java, "chat_db").build()

    @Provides
    @Singleton
    fun provideKtorClient() = HttpClient(Android){
        install(Logging){
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
    }
}