package com.edu.group.domain.usecase

import com.edu.common.data.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.repository.IStudentsRepo
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ViewModelScoped
class GetStudentsWithRatingUseCase @Inject constructor(private val studentsRepo: IStudentsRepo) {

    operator fun invoke(groupId: String): Flow<Result<List<StudentInfoDomain>>> {
        return studentsRepo.getStudentsWithRatingInGroup(groupId).flowOn(Dispatchers.IO).catch {
            emit(Result.Error(it))
        }
    }
}