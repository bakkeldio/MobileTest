package com.edu.test.data.room.repository

import com.edu.test.data.room.db.dao.TestDao
import com.edu.test.data.room.db.entity.QuestionEntity
import com.edu.test.data.room.db.mapper.*
import com.edu.test.domain.model.dbModels.QuestionAnswerDomain
import com.edu.test.domain.model.dbModels.QuestionDomain
import com.edu.test.domain.model.dbModels.QuestionWithAnswersDomain
import com.edu.test.domain.model.dbModels.TestDomain
import com.edu.test.domain.repository.ILocalTestsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTestsRepoImpl @Inject constructor(private val testDao: TestDao) : ILocalTestsRepo {

    override fun getUnPublishedTests(groupUid: String): Flow<List<TestDomain>> {
        return testDao.getTests(groupUid).map {
            it.map { test ->
                TestEntityToDomainMapper.mapToDomain(test)
            }
        }
    }

    override suspend fun createTestWithInitialQuestion(test: TestDomain): TestDomain {
        testDao.createTest(TestDomainToEntityMapper.mapFromDomain(test))
        testDao.insertQuestion(QuestionEntity(test_id = test.uid))
        return TestEntityToDomainMapper.mapToDomain(testDao.getTestWithId(test.uid))
    }

    override suspend fun getTestInfo(testId: String): TestDomain {
        return TestEntityToDomainMapper.mapToDomain(testDao.getTestWithId(testId))
    }

    override suspend fun updateTest(test: TestDomain) {
        return testDao.updateTest(TestDomainToEntityMapper.mapFromDomain(test))
    }

    override suspend fun deleteTestsByIds(ids: List<String>) {
        return testDao.deleteTestsByIds(ids)
    }

    override fun getQuestionsOfTest(testUid: String): Flow<List<QuestionDomain>> {
        return testDao.getTestQuestions(testUid).map { list ->
            list.map {
                QuestionEntityToDomainMapper.mapToDomain(it)
            }
        }
    }

    override suspend fun createQuestionInDb(testId: String) {
        return testDao.insertQuestion(
            QuestionEntity(
                test_id = testId
            )
        )
    }

    override suspend fun updateQuestionInDb(question: QuestionWithAnswersDomain) {
        val questionEntity = QuestionDomainToEntityMapper.mapFromDomain(question.question)
        testDao.updateQuestion(
            questionEntity.questionId,
            questionEntity.question,
            questionEntity.questionPoint,
            questionEntity.questionType
        )
    }

    override suspend fun deleteQuestionById(id: Int) {
        testDao.deleteQuestion(id)
    }

    override suspend fun deleteTest(testDomain: TestDomain) {
        testDao.deleteTest(TestDomainToEntityMapper.mapFromDomain(testDomain))
    }

    override fun getQuestionAnswers(questionId: Int): Flow<List<QuestionAnswerDomain>> {
        return testDao.getQuestionAnswers(questionId).map { answersEntity ->
            answersEntity.map { entity ->
                AnswerEntityToDomainMapper.mapToDomain(entity)
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun updateAnswer(answerDomain: QuestionAnswerDomain) {
        testDao.updateAnswer(AnswerDomainToEntityMapper.mapFromDomain(answerDomain))
    }

    override suspend fun deleteAnswer(answerDomain: QuestionAnswerDomain) {
        testDao.deleteAnswer(AnswerDomainToEntityMapper.mapFromDomain(answerDomain))
    }

    override suspend fun insertAnswer(answerDomain: QuestionAnswerDomain) {
        testDao.insertAnswer(AnswerDomainToEntityMapper.mapFromDomain(answerDomain))
    }

}