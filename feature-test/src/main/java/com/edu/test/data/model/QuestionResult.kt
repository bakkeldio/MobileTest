package com.edu.test.data.model

import android.os.Parcelable
import com.edu.common.domain.model.QuestionType
import kotlinx.parcelize.Parcelize


@Parcelize
data class QuestionResult(
    val uid: String,
    val userAnswer: List<Int>,
    val questionType: QuestionType,
    val answerOpenQuestion: String? = null
) : Parcelable