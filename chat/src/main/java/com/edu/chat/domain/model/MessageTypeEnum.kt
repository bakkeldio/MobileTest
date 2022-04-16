package com.edu.chat.domain.model

enum class MessageTypeEnum(val value: String) {
    Text("text"),
    Image("image");

    companion object {
        fun getTypeByValue(value: String) = values().find {
            it.value == value
        } ?: Text
    }
}