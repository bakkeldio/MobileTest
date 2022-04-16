package com.edu.test.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateQuestionDomain(
    val answers: HashMap<String, String>,
    val correctAnswer: List<String>,
    val questionType: String,
    val question: String,
    val point: Int
) : Parcelable