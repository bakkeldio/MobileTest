package com.edu.group.data.model

data class Group(
    val uid: String? = null,
    val groupName: String? = null,
    val description: String? = null,
    val studentsCount: Int = 0,
    val testsCount: Int = 0,
    val avatar: String? = null
)