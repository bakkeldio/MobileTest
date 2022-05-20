package com.edu.test.presentation.model

import com.edu.common.presentation.model.TestModel
import com.edu.test.domain.model.PassedTestDomain
import java.util.*

sealed class TestModelType(val testUid: String, val testTitle: String?, val testDate: Date) {
    data class Test(val test: TestModel) : TestModelType(test.uid, test.title, test.date)
    data class PassedTests(val passedTest: PassedTestDomain) :
        TestModelType(passedTest.uid, passedTest.testTitle, passedTest.testDate ?: Date())
}
