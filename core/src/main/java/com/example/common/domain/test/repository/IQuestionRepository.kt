package com.example.common.domain.test.repository

import com.example.common.data.Result
import com.example.common.domain.test.model.QuestionDomain

interface IQuestionRepository {

    suspend fun getQuestionsOfTest(testUid: String, groupId: String): Result<List<QuestionDomain>>
}