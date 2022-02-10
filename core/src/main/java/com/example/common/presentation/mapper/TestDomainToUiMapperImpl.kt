package com.example.common.presentation.mapper

import com.example.common.domain.model.TestDomainModel
import com.example.common.presentation.UiMapper
import com.example.common.presentation.model.TestModel

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