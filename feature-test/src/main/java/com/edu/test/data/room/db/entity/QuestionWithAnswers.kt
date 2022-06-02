package com.edu.test.data.room.db.entity

import androidx.room.Embedded
import androidx.room.Relation


data class QuestionWithAnswers(
    @Embedded
    val question: QuestionEntity,
    @Relation(parentColumn = "questionId", entityColumn = "question_id")
    val answers: List<AnswerEntity>
)