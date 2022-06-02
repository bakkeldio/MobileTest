package com.edu.test.data.room.db.mapper

import com.edu.common.data.mapper.Mapper
import com.edu.test.data.room.db.entity.TestEntity
import com.edu.test.domain.model.dbModels.TestDomain

object TestDomainToEntityMapper : Mapper.FromDomainMapper<TestDomain, TestEntity> {
    override fun mapFromDomain(model: TestDomain) = TestEntity(
        testName = model.name,
        testDate = model.date,
        duration = model.duration,
        testId = model.uid,
        groupUid = model.groupId
    )
}