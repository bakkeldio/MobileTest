package com.edu.mobiletest.ui.model

data class ProfileStudentUI(
    val name: String,
    val avatar: String?,
    val groupName: String,
    val groupId: String,
    val totalScore: Double,
    val rating: Int
)