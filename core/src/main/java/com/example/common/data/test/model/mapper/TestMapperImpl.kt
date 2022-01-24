package com.example.common.data.test.model.mapper

import com.example.common.domain.ApiMapper
import com.example.common.data.test.model.Test
import com.example.common.domain.test.model.TestDomainModel
import java.util.*

internal object TestMapperImpl: ApiMapper<Test, TestDomainModel> {
    override fun mapToDomain(apiEntity: Test): TestDomainModel {
        return TestDomainModel(
            uid = apiEntity.uid ?: throw IllegalArgumentException("id of test can not be null"),
            authorUid = apiEntity.authorUid ?: throw IllegalArgumentException("id of author can not be null"),
            date = apiEntity.date ?: Date(),
            time = apiEntity.time ?: 0,
            maxPoint = apiEntity.maxPoint ?: 0,
            title = apiEntity.title
        )
    }
}