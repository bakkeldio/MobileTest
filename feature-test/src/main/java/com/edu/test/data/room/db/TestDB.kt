package com.edu.test.data.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.edu.test.data.room.converter.Converters
import com.edu.test.data.room.db.dao.TestDao
import com.edu.test.data.room.db.entity.AnswerEntity
import com.edu.test.data.room.db.entity.QuestionEntity
import com.edu.test.data.room.db.entity.TestEntity

@Database(
    entities = [TestEntity::class, QuestionEntity::class, AnswerEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class TestDB : RoomDatabase() {

    abstract fun dao(): TestDao
}