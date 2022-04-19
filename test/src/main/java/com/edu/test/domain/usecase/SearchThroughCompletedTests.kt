package com.edu.test.domain.usecase

import com.edu.test.domain.repository.ITestsRepository
import javax.inject.Inject

class SearchThroughCompletedTests @Inject constructor(private val testsRepo: ITestsRepository) {

    suspend operator fun invoke(groupId: String, query: String) =
        testsRepo.searchThroughCompletedTests(query, groupId)
}