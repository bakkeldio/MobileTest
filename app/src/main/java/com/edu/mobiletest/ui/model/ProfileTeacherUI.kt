package com.edu.mobiletest.ui.model

import com.edu.group.domain.model.GroupDomain

data class ProfileTeacherUI(
    val uid: String,
    val fullName: String,
    val position: String?,
    val avatar: String?,
    val groups: List<GroupDomain>
)