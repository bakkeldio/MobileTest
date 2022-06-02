package com.edu.test.data.room.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "answer",
    foreignKeys = [ForeignKey(
        entity = QuestionEntity::class,
        parentColumns = ["questionId"],
        childColumns = ["question_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AnswerEntity(
    @PrimaryKey(autoGenerate = true)
    val answerId: Int = 0,
    val answer: String,
    val isCorrectAnswer: Boolean,
    val question_id: Int
)
