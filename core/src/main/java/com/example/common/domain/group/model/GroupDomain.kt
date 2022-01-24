package com.example.common.domain.group.model

data class GroupDomain(
    val uid: String,
    val groupName: String,
    val description: String,
    val studentsCount: Int,
    val testsCount: Int
)