package com.example.common.domain.test.model

data class StudentInfoDomain(
    val groupId: String,
    val name: String,
    val phone_number: String? = null,
    val uid: String
)