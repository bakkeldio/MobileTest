package com.example.group.domain.model

data class GroupDomain(
    val uid: String,
    val groupName: String,
    val description: String,
    val studentsCount: Int,
    val testsCount: Int
)