package com.edu.test.data.room.db.mapper

import com.edu.common.data.mapper.Mapper
import com.edu.test.data.room.db.entity.AnswerEntity
import com.edu.test.domain.model.dbModels.QuestionAnswerDomain

object AnswerDomainToEntityMapper : Mapper.FromDomainMapper<QuestionAnswerDomain, AnswerEntity> {
    override fun mapFromDomain(model: QuestionAnswerDomain): AnswerEntity {
        return AnswerEntity(
            model.id,
            answer = model.title,
            isCorrectAnswer = model.isCorrect,
            question_id = model.questionId
        )
    }
}