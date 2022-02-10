package com.example.group.data.di

import com.example.group.data.repository.GroupsRepoImpl
import com.example.group.domain.repository.IGroupsRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface GroupDataModule {

    @Binds
    fun bindGroupRepo(repo: GroupsRepoImpl): IGroupsRepo
}