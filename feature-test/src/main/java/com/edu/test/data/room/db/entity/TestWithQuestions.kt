package com.edu.test.data.room.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TestWithQuestions(
    @Embedded val test: TestEntity,
    @Relation(entity = QuestionEntity::class, parentColumn = "testId", entityColumn = "test_id")
    val questionsWithAnswers: List<QuestionWithAnswers>
)