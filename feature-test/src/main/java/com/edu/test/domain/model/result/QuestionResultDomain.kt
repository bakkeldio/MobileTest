package com.edu.test.domain.model.result

data class QuestionResultDomain(
    val answers: List<AnswerDomain>,
    val questionTitle: String,
    val questionPoint: Double,
    val questionUid: String,
    val correctAnswers: List<String>,
    val answerForOpenQuestion: String? = null
)