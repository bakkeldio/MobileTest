package com.edu.test.domain.usecase

import com.edu.common.data.Result
import com.edu.common.domain.model.QuestionDomain
import com.edu.common.domain.model.QuestionType
import com.edu.test.domain.model.AnswerDomain
import com.edu.test.domain.model.QuestionResultDomain
import com.edu.test.domain.repository.IQuestionRepository
import com.edu.test.domain.repository.ITestsRepository
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
                                testResult.answersToOpenQuestions,
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
        answersToMultipleChoiceQuestions: HashMap<String, List<String>>,
        answersToOpenQuestions: HashMap<String, String>,
        questions: List<QuestionDomain>
    ): List<QuestionResultDomain> {
        return questions.map { question ->
            val result = if (answersToMultipleChoiceQuestions.containsKey(question.uid)) {
                var questionPoint = 0.0
                val answers = answersToMultipleChoiceQuestions[question.uid] ?: emptyList()
                val answersResult = question.answersList.map { answerMap ->
                    val eachCorrectAnswerPoint = question.point / question.correctAnswer.size
                    if (question.questionType == QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS) {
                        if (answers.contains(answerMap.key) && question.correctAnswer.contains(
                                answerMap.key
                            )
                        ) {
                            questionPoint += eachCorrectAnswerPoint
                        }
                    }
                    AnswerDomain(
                        "${answerMap.key}. ${answerMap.value}",
                        answers.contains(answerMap.key),
                        question.correctAnswer.contains(answerMap.key)
                    )
                }
                if (question.questionType == QuestionType.MULTIPLE_CHOICE_ONE_ANSWER && question.correctAnswer.contains(
                        answers.firstOrNull()
                    )
                ) {
                    questionPoint += question.point
                }
                Pair(questionPoint, answersResult)
            } else {
                Pair(0.0, question.answersList.map { answer ->
                    AnswerDomain("${answer.key}. ${answer.value}")
                })
            }
            QuestionResultDomain(
                result.second,
                question.question,
                result.first,
                question.uid,
                question.correctAnswer,
                answersToOpenQuestions[question.uid]
            )
        }
    }
}