package com.edu.chat.domain.repository

import com.edu.chat.domain.model.*
import com.edu.common.data.Result
import kotlinx.coroutines.flow.Flow

interface IChatRepo {

    suspend fun getOrCreateChannel(otherUserId: String): Result<ChatChannel>

    fun getMessagesCountInChannel(channelId: String): Flow<Result<Long>>

    fun listenToNewMessagesFromChannels(): Flow<Result<List<Message>>>

    fun getEngagedChats(): Flow<Result<List<Pair<String, ChatChannel>>>>

    fun getMessagesOfChatChannelBeforeCurrentTimeStamp(channelId: String): Flow<Result<MessageComposed>>

    fun getPreviousMessagesOfChat(channelId: String): Flow<Result<MessageComposed>>

    fun getNewMessagesOfChat(channelId: String): Flow<Result<MessageComposed>>

    suspend fun sendNewMessageToUser(
        channel: ChatChannel,
        userId: String,
        messageUid: String,
        message: String,
        messageType: MessageTypeEnum
    ): Result<Unit>

    suspend fun markNewMessagesAsSeen(channelId: String, ids: List<String>): Result<Unit>

    suspend fun getChatMemberInfo(userId: String, role: String): Result<ChatMember>
}