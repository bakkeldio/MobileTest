package com.edu.test.presentation.model

import androidx.annotation.StringRes
import com.edu.test.R

enum class TeacherTestsTypeEnum(@StringRes private val id: Int) {
    MY(R.string.uploaded_tests),
    UN_PUBLISHED(R.string.un_published_tests);

    companion object {
        fun getValueByPosition(position: Int): Int {
            return values()[position].id
        }
    }
}