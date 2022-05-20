package com.edu.common.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Test(
    val authorUid: String ? = null,
    @ServerTimestamp
    val date: Date?= null,
    val time: Int ?= null,
    val maxPoint: Int ?= null,
    val title: String ?= null,
    val titleKeywords: List<String> = emptyList()
)