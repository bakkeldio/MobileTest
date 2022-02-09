package com.example.common.domain.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateQuestionDomain(
    val answers: HashMap<String, String>,
    val correctAnswers: List<String>,
    val questionType: String,
    val question: String,
    val point: Int
) : Parcelable