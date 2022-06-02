package com.edu.group.domain.usecase

import com.edu.common.domain.Result
import com.edu.group.domain.model.GroupDomain
import com.edu.group.domain.repository.IGroupsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetAllGroups @Inject constructor(private val repo: IGroupsRepo) {

    suspend operator fun invoke(query: String? = null): Result<List<GroupDomain>> {
        return query?.let {
            repo.searchThroughGroups(it)
        } ?: repo.getAvailableGroups()
    }
}