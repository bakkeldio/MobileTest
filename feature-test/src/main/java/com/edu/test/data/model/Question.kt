package com.edu.test.data.model

data class Question(
    val uid: String? = null,
    val question: String? = null,
    val correctAnswer: List<String> = emptyList(),
    val questionType: String? = null,
    val answers: HashMap<String, String> = hashMapOf(),
    val point: Int? = null
)