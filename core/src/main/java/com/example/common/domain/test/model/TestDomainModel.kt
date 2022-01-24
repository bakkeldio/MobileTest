package com.example.common.domain.test.model

import java.util.*

data class TestDomainModel(
    val uid: String,
    val authorUid: String,
    val date: Date,
    val time: Int,
    val maxPoint: Int,
    val title: String ? = null
)