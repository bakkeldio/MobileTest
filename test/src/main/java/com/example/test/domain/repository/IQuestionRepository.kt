package com.example.test.domain.repository

import com.example.common.data.Result
import com.example.common.domain.model.QuestionDomain

interface IQuestionRepository {

    suspend fun getQuestionsOfTest(testUid: String, groupId: String): Result<List<QuestionDomain>>
}