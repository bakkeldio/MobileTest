package com.edu.test.data.mapper

import com.edu.test.data.model.TestResult
import com.edu.common.data.mapper.Mapper
import com.edu.test.domain.model.result.TestResultDomain

object TestResultMapper: Mapper.ToDomainMapper<TestResult, TestResultDomain> {
    override fun mapToDomain(model: TestResult, uid: String): TestResultDomain {
        return TestResultDomain(
            model.studentUid,
            model.studentName,
            model.studentAvatar,
            model.totalPoints,
            answers = model.answers,
            answersToOpenQuestions = model.answersToOpenQuestions,
            model.pointsToOpenQuestions
        )
    }
}