package com.example.test.data.datamanager

import com.example.test.domain.model.CreateQuestionDomain
import com.example.test.presentation.model.NewTestDetail
import java.util.*


object TestCreationHandler {


    private var questions: HashMap<Int, CreateQuestionDomain> = hashMapOf()

    private val letters: List<String> = listOf("A", "B", "C", "D")


    fun createUpdateQuestion(
        position: Int,
        model: CreateQuestionDomain
    ) {
        questions[position] = model
    }

    fun getNewTestDetails(testName: String, availableDate: Date, duration: Int): NewTestDetail {
        return NewTestDetail(testName, availableDate, duration, questions.values.toList())
    }

    fun hasQuestionWithPosition(position: Int): Boolean {
        return questions.containsKey(position)
    }

    fun clear() {
        questions.clear()
    }

    fun checkIfLastQuestionWasSaved(pagesCount: Int): Boolean {
        return questions.size == pagesCount - 1
    }

    fun mapToCreateQuestionDomain(
        answers: HashMap<Int, String> = hashMapOf(),
        correctAnswers: HashMap<Int, String> = hashMapOf(),
        question: String = "",
        questionType: String = "",
        point: Int = 0
    ): CreateQuestionDomain {
        val ans = getCorrespondingLetterForAnswer(answers)
        val correctAns = getCorrespondingLetterForAnswer(correctAnswers)
        return CreateQuestionDomain(
            ans,
            correctAns.keys.map {
                it
            },
            questionType, question, point
        )
    }

    fun removeQuestion(position: Int) {
        if (questions.containsKey(position)) {
            questions.remove(position)
            questions = questions.mapKeysTo(hashMapOf()) {
                if (it.key > position) {
                    it.key - 1
                } else {
                    it.key
                }
            }
        }
    }

    fun getQuestionByPosition(position: Int): CreateQuestionDomain? {
        return questions[position]
    }

    private fun getCorrespondingLetterForAnswer(answers: HashMap<Int, String>): HashMap<String, String> {
        val ans = hashMapOf<String, String>()

        answers.keys.forEach { key ->
            ans[letters[key]] = answers[key]!!
        }
        return ans
    }
}