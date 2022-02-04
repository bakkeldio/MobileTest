package com.example.common.data.test.model.mapper

import com.example.common.data.test.model.Question
import com.example.common.domain.ApiMapper
import com.example.common.domain.test.model.QuestionDomain
import com.example.common.domain.test.model.QuestionType

internal object QuestionMapperImpl : ApiMapper<Question, QuestionDomain> {
    override fun mapToDomain(apiEntity: Question): QuestionDomain {
        return QuestionDomain(
            uid = apiEntity.uid ?: throw IllegalArgumentException("id can't be null"),
            answersCount = apiEntity.answers.size,
            questionType = QuestionType.getByValue(apiEntity.questionType),
            answersList = apiEntity.answers,
            correctAnswer = apiEntity.correctAnswer,
            question = apiEntity.question ?: throw IllegalArgumentException("question must not be empty or null"),
            point = apiEntity.point?.toDouble() ?: throw IllegalArgumentException("point for question must not be null")
        )
    }
}