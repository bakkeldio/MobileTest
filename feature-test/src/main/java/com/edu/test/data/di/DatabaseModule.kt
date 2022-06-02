package com.edu.test.data.di

import android.content.Context
import androidx.room.Room
import com.edu.test.data.room.db.TestDB
import com.edu.test.data.room.db.dao.TestDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabaseInstance(@ApplicationContext context: Context): TestDB =
        Room.databaseBuilder(context, TestDB::class.java, "Test_db")
            .fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideTestDao(roomDb: TestDB): TestDao = roomDb.dao()

}