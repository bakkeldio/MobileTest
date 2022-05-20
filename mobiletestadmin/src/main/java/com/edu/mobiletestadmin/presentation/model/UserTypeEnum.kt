package com.edu.mobiletestadmin.presentation.model

enum class UserTypeEnum(val position: Int, val type: String) {
    STUDENT(0, "student"),
    TEACHER(1, "teacher");

    companion object {
        fun getTypeByPosition(position: Int) = values()[position]
        fun getPositionByType(typeEnum: UserTypeEnum) = values().find {
            it == typeEnum
        }?.position ?: 0
    }
}