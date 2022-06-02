package com.edu.test.data.di

import com.edu.test.data.repository.QuestionRepoImpl
import com.edu.test.data.repository.TestsRepositoryImpl
import com.edu.test.data.room.repository.LocalTestsRepoImpl
import com.edu.test.domain.repository.ILocalTestsRepo
import com.edu.test.domain.repository.IQuestionRepository
import com.edu.test.domain.repository.ITestsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TestModule {
    @Binds
    fun bindRepository(repositoryImpl: TestsRepositoryImpl): ITestsRepository

    @Binds
    fun bindQuestionsRepo(repositoryImpl: QuestionRepoImpl): IQuestionRepository

    @Binds
    fun bindLocalTestRepo(repositoryImpl: LocalTestsRepoImpl): ILocalTestsRepo
}