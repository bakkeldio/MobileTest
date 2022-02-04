package com.example.common.presentation

interface UiMapper<T, E> {
    fun mapToUi(model: T): E
}