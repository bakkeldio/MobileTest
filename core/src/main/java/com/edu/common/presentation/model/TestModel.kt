package com.edu.common.presentation.model

import com.edu.common.domain.model.QuestionDomain
import java.util.*

data class TestModel(
    override val uid: String,
    val authorUid: String,
    val date: Date,
    val time: Int,
    val maxPoint: Int,
    val title: String,
    val status: TestStatusEnum = TestStatusEnum.NOT_STARTED,
    val questions: List<QuestionDomain>
): ItemForSelection