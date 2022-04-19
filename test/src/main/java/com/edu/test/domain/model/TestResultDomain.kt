package com.edu.test.domain.model

data class TestResultDomain(
    val studentUid: String,
    val studentName: String,
    val studentAvatar: String?,
    val totalPoints: Int = 0,
    val answers: HashMap<String, List<String>> = hashMapOf(),
    val answersToOpenQuestions: HashMap<String, String> = hashMapOf(),
    val pointsToOpenQuestions: HashMap<String, Double> = hashMapOf()
)