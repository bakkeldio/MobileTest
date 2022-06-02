package com.edu.test.data.room.db.mapper

import com.edu.common.data.mapper.Mapper
import com.edu.test.data.TestProcessHandler.letters
import com.edu.test.data.room.db.entity.AnswerEntity
import com.edu.test.data.room.db.entity.QuestionWithAnswers
import com.edu.test.domain.model.CreateQuestionDomain

object QuestionWithAnswersToCreateQuestionDomainMapper :
    Mapper.ToDomainMapper<QuestionWithAnswers, CreateQuestionDomain> {

    override fun mapToDomain(model: QuestionWithAnswers, uid: String): CreateQuestionDomain {
        return CreateQuestionDomain(
            buildMapOfAnswers(model.answers),
            findCorrectAnswers(model.answers),
            model.question.questionType,
            model.question.question,
            model.question.questionPoint,
            model.question.questionId
        )
    }

    private fun buildMapOfAnswers(answers: List<AnswerEntity>): HashMap<String, String> {
        val answersMap = hashMapOf<String, String>()
        answers.forEachIndexed { index, answerEntity ->
            answersMap[letters[index]] = answerEntity.answer
        }
        return answersMap
    }

    private fun findCorrectAnswers(answers: List<AnswerEntity>): MutableList<String> {
        val correctAnswers = mutableListOf<String>()
        answers.forEachIndexed { index, answerEntity ->
            if (answerEntity.isCorrectAnswer) {
                correctAnswers.add(letters[index])
            }
        }
        return correctAnswers
    }
}