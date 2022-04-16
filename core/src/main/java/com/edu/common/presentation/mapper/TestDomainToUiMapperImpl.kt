package com.edu.common.presentation.mapper

import com.edu.common.domain.model.TestDomainModel
import com.edu.common.presentation.UiMapper
import com.edu.common.presentation.model.TestModel

object TestDomainToUiMapperImpl : UiMapper<TestDomainModel, TestModel> {
    override fun mapToUi(model: TestDomainModel): TestModel {
        return TestModel(
            uid = model.uid,
            model.authorUid,
            model.date,
            model.time,
            model.maxPoint,
            model.title,
            questions = model.questions
        )
    }
}