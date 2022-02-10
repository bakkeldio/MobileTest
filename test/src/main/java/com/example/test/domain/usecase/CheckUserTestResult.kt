package com.example.test.domain.usecase

import com.example.common.data.Result
import com.example.common.domain.model.QuestionDomain
import com.example.common.domain.model.QuestionType
import com.example.test.domain.repository.IQuestionRepository
import com.example.test.domain.repository.ITestsRepository
import com.example.test.domain.model.AnswerDomain
import com.example.test.domain.model.QuestionResultDomain
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@ViewModelScoped
class CheckUserTestResult @Inject constructor(
    private val testRepo: ITestsRepository,
    private val questionRepo: IQuestionRepository
) {
    suspend operator fun invoke(
        groupUid: String,
        testUid: String
    ): Result<List<QuestionResultDomain>> {
        return when (val result = testRepo.getCompletedTestQuestions(groupUid, testUid)) {
            is Result.Success -> {
                val testResult = result.data
                    ?: return Result.Error(Exception("test result can't be null if it exists"))
                when (val questionResult = questionRepo.getQuestionsOfTest(testUid, groupUid)) {
                    is Result.Success -> {
                        val testQuestions = questionResult.data ?: emptyList()
                        Result.Success(
                            checkTheResultsWithOriginal(
                                testResult.answers,
                                testQuestions
                            )
                        )
                    }
                    is Result.Error -> questionResult
                }
            }
            is Result.Error -> result
        }
    }

    private fun checkTheResultsWithOriginal(
        userAnswers: HashMap<String, List<String>>,
        questions: List<QuestionDomain>
    ): List<QuestionResultDomain> {
        return questions.map {
            val result = if (userAnswers.containsKey(it.uid)) {
                var questionPoint = 0.0
                val answers = userAnswers[it.uid] ?: emptyList()
                val answersResult = it.answersList.map { answerMap ->
                    val eachCorrectAnswerPoint = it.point / it.correctAnswer.size
                    if (it.questionType == QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS) {
                        if (answers.contains(answerMap.key) && it.correctAnswer.contains(answerMap.key)) {
                            questionPoint += eachCorrectAnswerPoint
                        }
                    }
                    AnswerDomain(
                        "${answerMap.key}. ${answerMap.value}",
                        answers.contains(answerMap.key),
                        it.correctAnswer.contains(answerMap.key)
                    )
                }
                if (it.questionType == QuestionType.MULTIPLE_CHOICE_ONE_ANSWER && it.correctAnswer.contains(
                        answers.firstOrNull()
                    )
                ) {
                    questionPoint += it.point
                }
                Pair(questionPoint, answersResult)

            } else {
                Pair(0.0, emptyList())
            }
            QuestionResultDomain(result.second, it.question, result.first, it.uid, it.correctAnswer)
        }
    }
}