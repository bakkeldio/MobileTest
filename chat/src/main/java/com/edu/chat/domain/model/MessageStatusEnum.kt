package com.edu.chat.domain.model

enum class MessageStatusEnum(val value: Int) {
    NOT_SENT(0),
    SENT(1),
    SEEN(2);

    companion object {
        private val DEFAULT = SENT
        fun getByValue(value: Int) = values().find {
            it.value == value
        } ?: DEFAULT
    }
}