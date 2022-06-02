package com.edu.test.domain.model.dbModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnswersList(
    val deletedAnswers: MutableList<QuestionAnswerDomain> = mutableListOf(),
    val updateAnswers: MutableList<QuestionAnswerDomain> = mutableListOf(),
    val insertedAnswers: MutableList<QuestionAnswerDomain> = mutableListOf()
): Parcelable