package com.edu.test.domain.model.dbModels

import com.edu.common.presentation.model.ItemForSelection
import java.util.*

data class TestDomain(
    override val uid: String,
    val name: String,
    val date: Date,
    val duration: Int,
    val groupId: String = ""
): ItemForSelection