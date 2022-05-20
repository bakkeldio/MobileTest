package com.edu.chat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.edu.chat.data.db.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM message where channelId ==:channelId")
    fun getMessagesOfChannel(channelId: String): Flow<List<MessageEntity>>

    @Query("DELETE FROM message where uid ==:uid")
    suspend fun deleteMessageById(uid: String)

    @Query("SELECT*FROM message where uid ==:uid")
    suspend fun getMessageById(uid: String): List<MessageEntity>

}