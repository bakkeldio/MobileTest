package com.edu.test.domain.model.dbModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class QuestionWithAnswersDomain(
    val question: QuestionDomain,
    val answers: List<QuestionAnswerDomain> = emptyList(),
    val answersList: AnswersList = AnswersList()
) : Parcelable