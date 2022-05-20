package com.edu.test.domain.model

data class AnswerDomain(
    val title: String,
    val isSelected: Boolean = false,
    val isCorrect: Boolean = false
)