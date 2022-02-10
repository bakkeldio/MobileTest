package com.example.test.data.mapper

import com.example.test.data.model.TestResult
import com.example.common.domain.ApiMapper
import com.example.common.domain.model.TestResultDomain

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