package com.edu.test.data.di

import com.edu.test.data.QuestionRepoImpl
import com.edu.test.data.TestsRepositoryImpl
import com.edu.test.domain.repository.IQuestionRepository
import com.edu.test.domain.repository.ITestsRepository
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