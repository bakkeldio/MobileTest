package com.example.common.data.test.model

import com.example.common.domain.test.model.QuestionType


data class QuestionResult(
    val uid: String,
    val userAnswer: List<String>,
    val questionType: QuestionType
)