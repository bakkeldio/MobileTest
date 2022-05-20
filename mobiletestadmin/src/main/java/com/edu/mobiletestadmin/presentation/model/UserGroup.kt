package com.edu.mobiletestadmin.presentation.model

data class UserGroup(
    val groupUid: String,
    val groupName: String,
    val groupAvatar: String? = null,
    val description: String? = null
)