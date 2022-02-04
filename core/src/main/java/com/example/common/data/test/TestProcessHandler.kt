package com.example.common.data.test

import com.example.common.data.test.model.QuestionResult
import com.example.common.domain.test.model.QuestionDomain
import com.example.common.domain.test.model.QuestionType
import kotlin.math.roundToInt

object TestProcessHandler {

    private val userAnswersMap: HashMap<String, QuestionResult> = hashMapOf()

    private var questionsList: MutableList<QuestionDomain> = mutableListOf()


    fun put(result: QuestionResult) {
        userAnswersMap[result.uid] = result
    }

    fun setQuestions(list: List<QuestionDomain>) {
        questionsList.clear()
        questionsList.addAll(list)
    }

    fun mapTo(): HashMap<String, List<String>>{
        return userAnswersMap.mapValuesTo(hashMapOf()) {
            it.value.userAnswer
        }
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
                        if (userAnswersMap[model.uid]?.userAnswer?.first() == model.correctAnswer.first()) {
                            point += model.point
                        }
                    }
                    QuestionType.MULTIPLE_CHOICE_MULTIPLE_ANSWERS -> {
                        val eachPoint: Double = model.point / (model.correctAnswer.size).toDouble()
                        userAnswersMap[model.uid]?.userAnswer?.forEach {
                            if (model.correctAnswer.contains(it)) {
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
}