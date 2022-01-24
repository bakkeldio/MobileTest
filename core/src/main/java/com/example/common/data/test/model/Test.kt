package com.example.common.data.test.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

internal data class Test(
    val uid: String ?= null,
    val authorUid: String ? = null,
    @ServerTimestamp
    val date: Date?= null,
    val time: Int ?= null,
    val maxPoint: Int ?= null,
    val title: String ?= null
)