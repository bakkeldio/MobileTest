package com.edu.test.data.room.db.mapper

import com.edu.common.data.mapper.Mapper
import com.edu.test.data.room.db.entity.AnswerEntity
import com.edu.test.domain.model.dbModels.QuestionAnswerDomain

object AnswerEntityToDomainMapper : Mapper.ToDomainMapper<AnswerEntity, QuestionAnswerDomain> {
    override fun mapToDomain(model: AnswerEntity, uid: String): QuestionAnswerDomain {
        return QuestionAnswerDomain(
            model.answerId,
            model.answer,
            model.isCorrectAnswer,
            model.question_id
        )
    }
}