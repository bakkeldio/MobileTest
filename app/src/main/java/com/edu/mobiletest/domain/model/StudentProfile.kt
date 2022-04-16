package com.edu.mobiletest.domain.model

import com.edu.common.domain.model.IProfile

data class StudentProfile(
    val uid: String? = null,
    val avatarUrl: String? = null,
    val phone_number: String? = null,
    val name: String? = null,
    val groupId: String? = null
): IProfile