package com.example.test.domain.usecase

import com.example.common.data.Result
import com.example.common.domain.model.QuestionDomain
import com.example.test.domain.repository.IQuestionRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetTestQuestions @Inject constructor(private val questionsRepository: IQuestionRepository) {

    suspend operator fun invoke(groupId: String, testId: String): Result<List<QuestionDomain>> {
        return questionsRepository.getQuestionsOfTest(testId, groupId)
    }
}