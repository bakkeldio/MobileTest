package com.example.common.domain.test.model

enum class QuestionType(val type: String) {
    OPEN("open"),
    MULTIPLE_CHOICE_ONE_ANSWER("multiple_choice"),
    MULTIPLE_CHOICE_MULTIPLE_ANSWERS("multiple_choice_many");

    companion object {
        private val DEFAULT = MULTIPLE_CHOICE_ONE_ANSWER
        fun getByValue(type: String?): QuestionType {
            return values().find {
                it.type == type
            } ?: DEFAULT
        }
    }
}