package com.example.test.data.mapper

import com.example.test.data.model.Question
import com.example.common.domain.ApiMapper
import com.example.common.domain.model.QuestionDomain
import com.example.common.domain.model.QuestionType

object QuestionMapperImpl : ApiMapper<Question, QuestionDomain> {
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