package com.example.test.data.di

import com.example.test.data.QuestionRepoImpl
import com.example.test.data.TestsRepositoryImpl
import com.example.test.domain.repository.IQuestionRepository
import com.example.test.domain.repository.ITestsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface TestModule {
    @Binds
    fun bindRepository(repositoryImpl: TestsRepositoryImpl): ITestsRepository

    @Binds
    fun bindQuestionsRepo(repositoryImpl: QuestionRepoImpl): IQuestionRepository
}