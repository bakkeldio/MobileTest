package com.edu.test.domain.model

import android.os.Parcelable
import com.edu.test.domain.model.dbModels.AnswersList
import com.edu.test.domain.model.dbModels.QuestionAnswerDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateQuestionDomain(
    val answers: HashMap<String, String> = hashMapOf(),
    val correctAnswer: List<String> = emptyList(),
    val questionType: String,
    val question: String,
    val point: Int,
    val id: Int
) : Parcelable