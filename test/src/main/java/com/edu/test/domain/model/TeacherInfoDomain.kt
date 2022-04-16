package com.edu.test.domain.model

data class TeacherInfoDomain(
    val id: String,
    val groupsIds: List<String> = emptyList(),
    val name: String ?= null,
    val position: String ?= null
)