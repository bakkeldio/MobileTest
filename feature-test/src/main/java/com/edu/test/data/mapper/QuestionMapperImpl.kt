package com.edu.test.data.mapper

import com.edu.test.data.model.Question
import com.edu.common.domain.ApiMapper
import com.edu.common.domain.model.QuestionDomain
import com.edu.common.domain.model.QuestionType

object QuestionMapperImpl : ApiMapper<Question, QuestionDomain> {
    override fun mapToDomain(apiEntity: Question, uid: String): QuestionDomain {
        return QuestionDomain(
            uid = uid,
            answersCount = apiEntity.answers.size,
            questionType = QuestionType.getByValue(apiEntity.questionType),
            answersList = apiEntity.answers,
            correctAnswer = apiEntity.correctAnswer,
            question = apiEntity.question ?: throw IllegalArgumentException("question must not be empty or null"),
            point = apiEntity.point?.toDouble() ?: throw IllegalArgumentException("point for question must not be null")
        )
    }
}