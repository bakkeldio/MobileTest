package com.edu.group.data.di

import com.edu.group.data.repository.GroupsRepoImpl
import com.edu.group.data.repository.StudentsRepoImpl
import com.edu.group.domain.repository.IGroupsRepo
import com.edu.common.domain.repository.IStudentsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface GroupDataModule {

    @Binds
    fun bindGroupRepo(repo: GroupsRepoImpl): IGroupsRepo

    @Binds
    fun bindStudentsRepo(repo: StudentsRepoImpl): IStudentsRepo
}