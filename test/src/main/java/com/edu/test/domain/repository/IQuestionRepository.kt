package com.edu.test.domain.repository

import com.edu.common.data.Result
import com.edu.common.domain.model.QuestionDomain

interface IQuestionRepository {

    suspend fun getQuestionsOfTest(testUid: String, groupId: String): Result<List<QuestionDomain>>
}