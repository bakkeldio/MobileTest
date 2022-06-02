package com.edu.test.data.room.db.mapper

import com.edu.common.data.mapper.Mapper
import com.edu.test.data.room.db.entity.QuestionEntity
import com.edu.test.domain.model.dbModels.QuestionDomain

object QuestionDomainToEntityMapper : Mapper.FromDomainMapper<QuestionDomain, QuestionEntity> {
    override fun mapFromDomain(model: QuestionDomain): QuestionEntity {
        return QuestionEntity(
            question = model.title,
            questionPoint = model.point,
            questionType = model.questionType.type,
            test_id = model.testId,
            questionId = model.id
        )
    }
}