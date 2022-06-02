package com.edu.test.data.room.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "test")
data class TestEntity(
    @PrimaryKey
    val testId: String,
    val testName: String,
    val testDate: Date,
    val duration: Int,
    val groupUid: String
)