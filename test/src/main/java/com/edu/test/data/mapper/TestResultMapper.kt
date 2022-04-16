package com.edu.test.data.mapper

import com.edu.test.data.model.TestResult
import com.edu.common.domain.ApiMapper
import com.edu.common.domain.model.TestResultDomain

object TestResultMapper: ApiMapper<TestResult, TestResultDomain> {
    override fun mapToDomain(apiEntity: TestResult, uid: String): TestResultDomain {
        return TestResultDomain(
            uid = uid,
            testTitle = apiEntity.title ?: throw IllegalArgumentException("test title can't be null"),
            totalScore = apiEntity.totalPoints,
            answers = apiEntity.answers,
            answersToOpenQuestions = apiEntity.answersToOpenQuestions
        )
    }
}