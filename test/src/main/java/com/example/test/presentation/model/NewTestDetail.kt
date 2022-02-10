package com.example.test.presentation.model

import com.example.test.domain.model.CreateQuestionDomain
import java.util.*

class NewTestDetail(
    val testName: String,
    val availableDate: Date,
    val duration: Int,
    val questions: List<CreateQuestionDomain>
)