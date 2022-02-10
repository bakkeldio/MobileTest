package com.example.test.data.model

data class TestResult(
    val uid: String? = null,
    val title: String? = null,
    val maxPoint: Int = 0,
    val answers: HashMap<String, List<String>> = hashMapOf()
)