package com.edu.mobiletestadmin.presentation.model

data class NewUser(
    val name: String,
    val email: String,
    val password: String,
    val userType: UserTypeEnum,
    val groups: List<String>,
    val nameKeywords: List<String> = emptyList()
)