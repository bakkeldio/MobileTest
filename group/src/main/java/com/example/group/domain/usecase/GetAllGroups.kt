package com.example.group.domain.usecase

import com.example.common.data.Result
import com.example.group.domain.model.GroupDomain
import com.example.group.domain.repository.IGroupsRepo
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