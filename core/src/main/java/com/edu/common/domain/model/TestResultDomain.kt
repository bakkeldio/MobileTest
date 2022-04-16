package com.edu.common.domain.model

class TestResultDomain(
    val uid: String,
    val testTitle: String,
    val totalScore: Int,
    val answers: HashMap<String, List<String>>,
    val answersToOpenQuestions: HashMap<String, String>
)