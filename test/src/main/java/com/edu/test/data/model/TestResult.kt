package com.edu.test.data.model

data class TestResult(
    val title: String? = null,
    val totalPoints: Int = 0,
    val answers: HashMap<String, List<String>> = hashMapOf(),
    val answersToOpenQuestions: HashMap<String, String> = hashMapOf()
)