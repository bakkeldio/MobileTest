package com.edu.group.domain.usecase

import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.repository.IStudentsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AddStudentToGroupUseCase @Inject constructor(private val studentsRepo: IStudentsRepo) {

    suspend operator fun invoke(groupId: String, student: StudentInfoDomain) =
        studentsRepo.addStudentToGroup(groupId, student)
}