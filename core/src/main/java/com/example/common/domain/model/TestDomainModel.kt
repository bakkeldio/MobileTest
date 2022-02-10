package com.example.common.domain.model

import java.util.*

data class TestDomainModel(
    val uid: String,
    val authorUid: String,
    val date: Date,
    val time: Int,
    val maxPoint: Int,
    val title: String ? = null,
    val questions: List<QuestionDomain> = emptyList()
)