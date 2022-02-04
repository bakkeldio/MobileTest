package com.example.test.presentation.model

import androidx.annotation.StringRes
import com.example.test.R

enum class UserTestsTypeEnum(@StringRes val id: Int) {
    ALL(R.string.all_tests),
    PASSED(R.string.passed_tests);

    companion object {
        fun getValueByPosition(position: Int): Int {
            return values()[position].id
        }
    }
}