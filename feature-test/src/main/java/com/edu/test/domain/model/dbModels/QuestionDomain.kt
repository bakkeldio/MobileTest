package com.edu.test.domain.model.dbModels

import android.os.Parcelable
import com.edu.common.domain.model.QuestionType
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionDomain(
    val id: Int,
    val title: String,
    val point: Int,
    val questionType: QuestionType,
    val testId: String
): Parcelable