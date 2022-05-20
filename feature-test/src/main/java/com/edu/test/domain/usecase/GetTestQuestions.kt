package com.edu.test.domain.usecase

import com.edu.common.data.Result
import com.edu.common.domain.model.QuestionDomain
import com.edu.test.domain.repository.IQuestionRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetTestQuestions @Inject constructor(private val questionsRepository: IQuestionRepository) {

    suspend operator fun invoke(groupId: String, testId: String): Result<List<QuestionDomain>> {
        return questionsRepository.getQuestionsOfTest(testId, groupId)
    }
}