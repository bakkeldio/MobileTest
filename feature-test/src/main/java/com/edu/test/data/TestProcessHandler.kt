package com.edu.test.data

import com.edu.common.domain.model.QuestionDomain
import com.edu.common.domain.model.QuestionType
import com.edu.test.data.model.QuestionResult
import kotlin.math.roundToInt

object TestProcessHandler {


    val letters: List<String> = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
        "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )

    val userAnswersMap: HashMap<String, QuestionResult> = hashMapOf()

    private var questionsList: MutableList<QuestionDomain> = mutableListOf()


    fun put(result: QuestionResult) {
        userAnswersMap[result.uid] = result
    }

    fun setQuestions(list: List<QuestionDomain>) {
        questionsList.clear()
        questionsList.addAll(list)
    }

    fun getUserAnswers() = userAnswersMap

    fun mapTo(): HashMap<String, List<String>> {
        return userAnswersMap.mapValuesTo(hashMapOf()) {
            it.value.userAnswer.map { position ->
                convertToLetterAccordingToPosition(position)
            }
        }.also { answers ->
            getAnswersToOpenQuestions().forEach {
                answers.remove(it.key)
            }
        }
    }

    fun getAnswersToOpenQuestions() = userAnswersMap.filter {
        it.value.questionType == QuestionType.OPEN && it.value.answerOpenQuestion != null
    }.mapValuesTo(hashMapOf()) {
        it.value.answerOpenQuestion!!
    }


    fun clear() {
        questionsList.clear()
        userAnswersMap.clear()
    }

    fun calculateTotalScore(): Int {
        var point = 0.0
        questionsList.forEach { model ->
            if (userAnswersMap.containsKey(model.uid)) {
                when (model.questionType) {
                    QuestionType.MULTIPLE_CHOICE_ONE_ANSWER -> {
                        val position = userAnswersMap[model.uid]?.userAnswer?.firstOrNull() ?: 0
                        if (convertToLetterAccordingToPosition(position) == model.correctAnswer.first()) {
                            point += model.point
                        }
                    }
                    QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS -> {
                        val eachPoint: Double = model.point / (model.correctAnswer.size).toDouble()
                        userAnswersMap[model.uid]?.userAnswer?.forEach {
                            if (model.correctAnswer.contains(convertToLetterAccordingToPosition(it))) {
                                point += eachPoint
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
        return point.roundToInt()
    }

    private fun convertToLetterAccordingToPosition(position: Int) =
        letters[position]
}