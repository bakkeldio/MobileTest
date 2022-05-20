package com.edu.test.data.model

import com.edu.test.domain.model.CreateQuestionDomain
import java.util.*

class NewTestDetail(
    val testName: String,
    val availableDate: Date,
    val duration: Int,
    val questions: List<CreateQuestionDomain>
)