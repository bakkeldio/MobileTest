package com.edu.test.data.room.db.mapper

import com.edu.common.data.mapper.Mapper
import com.edu.test.data.room.db.entity.TestEntity
import com.edu.test.domain.model.dbModels.TestDomain

object TestEntityToDomainMapper : Mapper.ToDomainMapper<TestEntity, TestDomain> {
    override fun mapToDomain(model: TestEntity, uid: String): TestDomain =
        TestDomain(model.testId, model.testName, model.testDate, model.duration, model.groupUid)
}