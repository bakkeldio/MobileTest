package com.edu.test.data.mapper

import com.edu.test.data.model.Question
import com.edu.common.data.mapper.Mapper
import com.edu.common.domain.model.QuestionDomain
import com.edu.common.domain.model.QuestionType

object QuestionMapperImpl : Mapper.ToDomainMapper<Question, QuestionDomain> {
    override fun mapToDomain(model: Question, uid: String): QuestionDomain {
        return QuestionDomain(
            uid = uid,
            answersCount = model.answers.size,
            questionType = QuestionType.getByValue(model.questionType),
            answersList = model.answers,
            correctAnswer = model.correctAnswer,
            question = model.question ?: throw IllegalArgumentException("question must not be empty or null"),
            point = model.point?.toDouble() ?: throw IllegalArgumentException("point for question must not be null")
        )
    }
}