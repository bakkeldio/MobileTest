package com.edu.chat.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.edu.chat.data.db.dao.MessageDao
import com.edu.chat.data.db.entity.MessageEntity
import com.edu.chat.data.db.Converters

@Database(entities = [MessageEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}