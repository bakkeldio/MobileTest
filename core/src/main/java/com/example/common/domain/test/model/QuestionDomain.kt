package com.example.common.domain.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class QuestionDomain(
    val uid: String,
    val answersCount: Int,
    val question: String,
    val questionType: QuestionType,
    val answersList: HashMap<String, String>,
    val correctAnswer: List<String>,
    val point: Double
): Parcelable