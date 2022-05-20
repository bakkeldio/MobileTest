package com.edu.mobiletestadmin.data.model

class Teacher(
    val uid: String,
    val name: String,
    val avatarUrl: String?,
    val groupIds: List<String>
) {
    constructor() : this("", "", null, emptyList())
}