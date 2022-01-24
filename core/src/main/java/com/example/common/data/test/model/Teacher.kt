package com.example.common.data.test.model

internal data class Teacher(
    val id: String ?= null,
    val groupsIds: List<String> = emptyList(),
    val name: String ?= null,
    val position: String ?= null
)