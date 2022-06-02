package com.edu.test.data.room.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = TestEntity::class,
        parentColumns = ["testId"],
        childColumns = ["test_id"],
        onDelete = ForeignKey.CASCADE
    )],
    tableName = "question"
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    var questionId: Int = 0,
    val question: String = "",
    val questionPoint: Int = 0,
    val questionType: String = "",
    val test_id: String? = null
)