package com.example.common.domain.test.model

class TestResultDomain(
    val uid: String,
    val testTitle: String,
    val totalScore: Int,
    val answers: HashMap<String, List<String>>
)