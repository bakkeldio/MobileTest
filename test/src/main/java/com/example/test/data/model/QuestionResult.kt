package com.example.test.data.model

import com.example.common.domain.model.QuestionType


data class QuestionResult(
    val uid: String,
    val userAnswer: List<String>,
    val questionType: QuestionType
)