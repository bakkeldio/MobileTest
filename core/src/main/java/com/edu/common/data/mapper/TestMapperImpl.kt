package com.edu.common.data.mapper

import com.edu.common.data.model.Test
import com.edu.common.domain.model.TestDomainModel
import java.util.*

object TestMapperImpl: Mapper.ToDomainMapper<Test, TestDomainModel> {
    override fun mapToDomain(model: Test, uid: String): TestDomainModel {
        return TestDomainModel(
            uid = uid,
            authorUid = model.authorUid ?: "",
            date = model.date ?: Date(),
            time = model.time ?: 0,
            maxPoint = model.maxPoint ?: 0,
            title = model.title ?: throw IllegalArgumentException("test title can't be null")
        )
    }
}