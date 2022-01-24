package com.example.common.di

import com.example.common.data.groups.GroupsRepoImpl
import com.example.common.domain.group.repository.IGroupsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface GroupsModule {

    @Binds
    fun bindGroupsRepo(groupsRepoImpl: GroupsRepoImpl): IGroupsRepo
}