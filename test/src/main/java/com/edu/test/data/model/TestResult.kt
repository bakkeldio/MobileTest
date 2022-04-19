package com.edu.test.data.model

import java.util.*
import kotlin.collections.HashMap

data class TestResult(
    val studentUid: String,
    val studentName: String,
    val studentAvatar: String?,
    val testUid: String,
    val testTitle: String?,
    val testDate: Date?,
    val totalPoints: Int,
    val answers: HashMap<String, List<String>>,
    val answersToOpenQuestions: HashMap<String, String>,
    val pointsToOpenQuestions: HashMap<String, Double> = hashMapOf()
) {
    constructor() : this("", "", null, "", "", null, 0, hashMapOf(), hashMapOf())
}