package com.edu.test.data.room.db.dao

import androidx.room.*
import com.edu.test.data.room.db.entity.AnswerEntity
import com.edu.test.data.room.db.entity.QuestionEntity
import com.edu.test.data.room.db.entity.TestEntity
import com.edu.test.data.room.db.entity.TestWithQuestions
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {


    @Query("SELECT * FROM test where groupUid=:groupUid")
    fun getTests(groupUid: String): Flow<List<TestEntity>>

    @Query("select * from question where test_id=:testId")
    fun getTestQuestions(testId: String): Flow<List<QuestionEntity>>

    @Query("select * from test where testId=:testId")
    suspend fun getTestWithQuestions(testId: String): TestWithQuestions

    @Query("select * from test where testId =:id")
    suspend fun getTestWithId(id: String): TestEntity

    @Delete
    suspend fun deleteTest(test: TestEntity)

    @Query("delete from test where testId in(:ids)")
    suspend fun deleteTestsByIds(ids: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTest(test: TestEntity)

    @Update
    suspend fun updateTest(test: TestEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Query("UPDATE question SET question = :question, questionPoint = :point, questionType = :type WHERE questionId = :id")
    suspend fun updateQuestion(id: Int, question: String, point: Int, type: String)

    @Query("delete from question where questionId=:id")
    suspend fun deleteQuestion(id: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAnswer(answer: AnswerEntity)

    @Delete
    suspend fun deleteAnswer(answer: AnswerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answers: AnswerEntity)

    @Query("SELECT * FROM answer where question_id=:questionId")
    fun getQuestionAnswers(questionId: Int): Flow<List<AnswerEntity>>


}