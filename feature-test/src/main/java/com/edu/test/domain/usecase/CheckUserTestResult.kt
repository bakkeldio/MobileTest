package com.edu.test.domain.usecase

import com.edu.common.domain.Result
import com.edu.common.domain.model.QuestionDomain
import com.edu.common.domain.model.QuestionType
import com.edu.test.domain.model.result.AnswerDomain
import com.edu.test.domain.model.result.QuestionResultDomain
import com.edu.test.domain.repository.IQuestionRepository
import com.edu.test.domain.repository.ITestsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@ViewModelScoped
class CheckUserTestResult @Inject constructor(
    private val testRepo: ITestsRepository,
    private val questionRepo: IQuestionRepository
) {
    suspend operator fun invoke(
        studentUid: String?,
        groupUid: String,
        testUid: String
    ): Flow<Result<List<QuestionResultDomain>>> {

        return flow {
            testRepo.getCompletedTestQuestions(studentUid, groupUid, testUid).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val testResult = result.data ?: return@collect
                        when (val questionResult =
                            questionRepo.getQuestionsOfTest(testUid, groupUid)) {
                            is Result.Success -> {
                                val testQuestions = questionResult.data ?: emptyList()
                                emit(
                                    Result.Success(
                                        checkTheResultsWithOriginal(
                                            testResult.answers,
                                            testResult.pointsToOpenQuestions,
                                            testResult.answersToOpenQuestions,
                                            testQuestions
                                        )
                                    )
                                )
                            }
                            is Result.Error -> emit(questionResult)
                        }
                    }
                    is Result.Error -> emit(result)
                }
            }
        }.catch {
            emit(Result.Error(it))
        }
    }

    private fun checkTheResultsWithOriginal(
        answersToMultipleChoiceQuestions: HashMap<String, List<String>>,
        openQuestionsPoints: HashMap<String, Double>,
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
                val point = if (openQuestionsPoints.containsKey(question.uid)) {
                    openQuestionsPoints[question.uid]!!
                } else {
                    0.0
                }
                Pair(point, question.answersList.map { answer ->
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