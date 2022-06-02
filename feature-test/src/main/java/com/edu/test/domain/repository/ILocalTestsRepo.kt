package com.edu.test.domain.repository

import com.edu.test.domain.model.dbModels.QuestionAnswerDomain
import com.edu.test.domain.model.dbModels.QuestionDomain
import com.edu.test.domain.model.dbModels.QuestionWithAnswersDomain
import com.edu.test.domain.model.dbModels.TestDomain
import kotlinx.coroutines.flow.Flow

interface ILocalTestsRepo {

    fun getUnPublishedTests(groupUid: String): Flow<List<TestDomain>>

    suspend fun createTestWithInitialQuestion(test: TestDomain): TestDomain

    suspend fun getTestInfo(testId: String): TestDomain

    suspend fun updateTest(test: TestDomain)

    suspend fun deleteTestsByIds(ids: List<String>)

    fun getQuestionsOfTest(testUid: String): Flow<List<QuestionDomain>>

    suspend fun createQuestionInDb(testId: String)

    suspend fun updateQuestionInDb(question: QuestionWithAnswersDomain)

    suspend fun deleteQuestionById(id: Int)

    suspend fun deleteTest(testDomain: TestDomain)

    fun getQuestionAnswers(questionId: Int): Flow<List<QuestionAnswerDomain>>

    suspend fun updateAnswer(answerDomain: QuestionAnswerDomain)

    suspend fun deleteAnswer(answerDomain: QuestionAnswerDomain)

    suspend fun insertAnswer(answerDomain: QuestionAnswerDomain)
}