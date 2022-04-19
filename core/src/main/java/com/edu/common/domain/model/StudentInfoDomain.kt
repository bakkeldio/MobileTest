package com.edu.common.domain.model

data class StudentInfoDomain(
    val groupId: String?,
    val name: String,
    val phone_number: String?,
    val avatarUrl: String?,
    val overallScore: Double,
    val uid: String,
    val registrationTokens: MutableList<String> = mutableListOf()
) {
    constructor() : this(null, "", "", null, 0.0, "", mutableListOf())
}