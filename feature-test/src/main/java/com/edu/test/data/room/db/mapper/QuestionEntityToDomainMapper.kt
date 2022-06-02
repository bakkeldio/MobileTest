package com.edu.test.data.room.db.mapper

import com.edu.common.data.mapper.Mapper
import com.edu.common.domain.model.QuestionType
import com.edu.test.data.room.db.entity.QuestionEntity
import com.edu.test.domain.model.dbModels.QuestionDomain

object QuestionEntityToDomainMapper : Mapper.ToDomainMapper<QuestionEntity, QuestionDomain> {
    override fun mapToDomain(model: QuestionEntity, uid: String): QuestionDomain {
        return QuestionDomain(
            model.questionId,
            model.question,
            model.questionPoint,
            QuestionType.getByValue(model.questionType),
            model.test_id!!
        )
    }


}