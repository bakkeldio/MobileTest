package com.example.common.di

import com.example.common.data.test.QuestionRepoImpl
import com.example.common.data.test.TestsRepositoryImpl
import com.example.common.domain.test.repository.IQuestionRepository
import com.example.common.domain.test.repository.ITestsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface TestModule {

    @Binds
    fun bindRepository(repositoryImpl: TestsRepositoryImpl): ITestsRepository

    @Binds
    fun bindQuestionsRepo(repositoryImpl: QuestionRepoImpl): IQuestionRepository

}