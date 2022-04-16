package com.edu.common.domain.model

data class TeacherProfile(
    val uid: String? = null,
    val avatarUrl: String? = null,
    val name: String? = null,
    val groupIds: List<String>? = null,
    val position: String? = null,
    val registrationTokens: MutableList<String> = mutableListOf()
): IProfile