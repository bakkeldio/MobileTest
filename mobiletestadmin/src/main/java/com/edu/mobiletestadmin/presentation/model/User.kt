package com.edu.mobiletestadmin.presentation.model

data class User(
    val uid: String,
    val avatar: String?,
    val groupId: List<String>,
    val name: String,
    val userType: UserTypeEnum
)