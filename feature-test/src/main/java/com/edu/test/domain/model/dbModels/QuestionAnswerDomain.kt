package com.edu.test.domain.model.dbModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionAnswerDomain(
    val id: Int = 0,
    val title: String,
    val isCorrect: Boolean,
    val questionId: Int
) : Parcelable