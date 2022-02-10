package com.example.common.data.mapper

import com.example.common.domain.ApiMapper
import com.example.common.data.model.Test
import com.example.common.domain.model.TestDomainModel
import java.util.*

object TestMapperImpl: ApiMapper<Test, TestDomainModel> {
    override fun mapToDomain(apiEntity: Test): TestDomainModel {
        return TestDomainModel(
            uid = apiEntity.uid ?: throw IllegalArgumentException("id of test can not be null"),
            authorUid = apiEntity.authorUid ?: "",
            date = apiEntity.date ?: Date(),
            time = apiEntity.time ?: 0,
            maxPoint = apiEntity.maxPoint ?: 0,
            title = apiEntity.title
        )
    }
}