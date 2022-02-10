package com.example.group.data.model

data class Group(
    val uid: String? = null,
    val group_name: String? = null,
    val description: String? = null,
    val students_count: Int = 0,
    val tests_count: Int = 0
)