package com.edu.mobiletestadmin.data.model

import com.edu.mobiletestadmin.presentation.model.User
import com.edu.mobiletestadmin.presentation.model.UserGroup
import com.edu.mobiletestadmin.presentation.model.UserTypeEnum

object MappersImpl {

    fun mapGroupInfoToUserGroup(groupInfo: GroupInfo) = UserGroup(
        groupInfo.uid ?: throw IllegalArgumentException("uid can't be null"),
        groupInfo.groupName ?: throw IllegalArgumentException("group name can't be null"),
        groupInfo.avatar,
        groupInfo.description
    )

    fun mapStudentModelToUser(student: Student) = User(
        student.uid,
        student.avatarUrl,
        listOf(student.groupId),
        student.name,
        UserTypeEnum.STUDENT
    )

    fun mapTeacherModelToUser(teacher: Teacher) = User(
        teacher.uid,
        teacher.avatarUrl,
        teacher.groupIds,
        teacher.name,
        UserTypeEnum.TEACHER
    )

}