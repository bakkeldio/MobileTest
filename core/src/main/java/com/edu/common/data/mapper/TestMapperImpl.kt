package com.edu.common.data.mapper

import com.edu.common.domain.ApiMapper
import com.edu.common.data.model.Test
import com.edu.common.domain.model.TestDomainModel
import java.util.*

object TestMapperImpl: ApiMapper<Test, TestDomainModel> {
    override fun mapToDomain(apiEntity: Test, uid: String): TestDomainModel {
        return TestDomainModel(
            uid = uid,
            authorUid = apiEntity.authorUid ?: "",
            date = apiEntity.date ?: Date(),
            time = apiEntity.time ?: 0,
            maxPoint = apiEntity.maxPoint ?: 0,
            title = apiEntity.title
        )
    }
}