package com.example.common.data.test.model.mapper

import com.example.common.data.test.model.TestResult
import com.example.common.domain.ApiMapper
import com.example.common.domain.test.model.TestResultDomain

object TestResultMapper: ApiMapper<TestResult, TestResultDomain> {
    override fun mapToDomain(apiEntity: TestResult): TestResultDomain {
        return TestResultDomain(
            uid = apiEntity.uid ?: throw IllegalArgumentException("id of test can't be null"),
            testTitle = apiEntity.title ?: throw IllegalArgumentException("test title can't be null"),
            totalScore = apiEntity.maxPoint,
            answers = apiEntity.answers
        )
    }
}