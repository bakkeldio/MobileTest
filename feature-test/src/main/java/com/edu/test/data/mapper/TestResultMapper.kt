package com.edu.test.data.mapper

import com.edu.test.data.model.TestResult
import com.edu.common.domain.ApiMapper
import com.edu.test.domain.model.TestResultDomain

object TestResultMapper: ApiMapper<TestResult, TestResultDomain> {
    override fun mapToDomain(apiEntity: TestResult, uid: String): TestResultDomain {
        return TestResultDomain(
            apiEntity.studentUid,
            apiEntity.studentName,
            apiEntity.studentAvatar,
            apiEntity.totalPoints,
            answers = apiEntity.answers,
            answersToOpenQuestions = apiEntity.answersToOpenQuestions,
            apiEntity.pointsToOpenQuestions
        )
    }
}