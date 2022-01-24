package com.example.group.domain.usecase

import com.example.common.data.Result
import com.example.common.domain.group.repository.IGroupsRepo
import com.example.common.domain.test.model.TestDomainModel
import javax.inject.Inject

class GetGroupTests @Inject constructor(private val groupsRepo: IGroupsRepo) {

    suspend operator fun invoke(groupId: String): Result<List<TestDomainModel>>{
        return groupsRepo.getGroupTests(groupId)
    }
}