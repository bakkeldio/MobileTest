package com.edu.test.data.datamanager

import com.edu.test.data.model.NewTestDetail
import com.edu.test.domain.model.CreateQuestionDomain
import java.util.*


object TestCreationHandler {


    private var questions: HashMap<Int, CreateQuestionDomain> = hashMapOf()

    val letters: List<String> = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
        "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )

    private var newTest: NewTestDetail? = null

    fun createUpdateQuestion(
        position: Int,
        model: CreateQuestionDomain
    ) {
        questions[position] = model
    }

    fun saveNewTest(testName: String, availableDate: Date, duration: Int) {
        newTest = NewTestDetail(testName, availableDate, duration, questions.values.toList())
    }

    fun getNewTest(): NewTestDetail? {
        return newTest
    }

    fun getMaxPointForNewTest(): Int {
        var point = 0
        questions.forEach {
            point += it.value.point
        }
        return point
    }

    fun hasQuestionWithPosition(position: Int): Boolean {
        return questions.containsKey(position)
    }

    fun clear() {
        newTest = null
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

    private fun getCorrespondingLetterForAnswer(answers: HashMap<Int, String>): HashMap<String, String> {
        val ans = hashMapOf<String, String>()

        answers.keys.forEach { key ->
            ans[letters[key]] = answers[key]!!
        }
        return ans
    }
}