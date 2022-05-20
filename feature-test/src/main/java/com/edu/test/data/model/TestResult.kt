package com.edu.test.data.model

import java.util.*

data class TestResult(
    val studentUid: String,
    val studentName: String,
    val studentAvatar: String?,
    val testUid: String,
    val testTitle: String?,
    val testTitleKeywords: List<String>,
    val testDate: Date?,
    val totalPoints: Int,
    val groupUid: String,
    val answers: HashMap<String, List<String>>,
    val answersToOpenQuestions: HashMap<String, String>,
    val pointsToOpenQuestions: HashMap<String, Double> = hashMapOf()
) {
    constructor() : this(
        "",
        "",
        null,
        "",
        "",
        emptyList(),
        null,
        0,
        "",
        hashMapOf(),
        hashMapOf()
    )
}