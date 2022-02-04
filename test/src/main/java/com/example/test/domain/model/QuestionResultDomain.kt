package com.example.test.domain.model

data class QuestionResultDomain(
    val answers: List<AnswerDomain>,
    val questionTitle: String,
    val questionPoint: Double,
    val questionUid: String,
    val correctAnswers: List<String>
)