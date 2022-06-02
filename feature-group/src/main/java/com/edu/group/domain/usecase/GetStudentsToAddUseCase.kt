package com.edu.group.domain.usecase

import com.edu.common.domain.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.repository.IStudentsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@ViewModelScoped
class GetStudentsToAddUseCase @Inject constructor(private val studentsRepo: IStudentsRepo) {

    suspend operator fun invoke(): Flow<Result<List<StudentInfoDomain>>> {
        return studentsRepo.getStudentsToAdd().catch {
            emit(Result.Error(it))
        }
    }
}