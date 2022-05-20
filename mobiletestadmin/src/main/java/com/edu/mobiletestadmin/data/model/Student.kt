package com.edu.mobiletestadmin.data.model

class Student(
    val uid: String,
    val name: String,
    val avatarUrl: String?,
    val groupId: String
) {
    constructor() : this("", "", null, "")
}