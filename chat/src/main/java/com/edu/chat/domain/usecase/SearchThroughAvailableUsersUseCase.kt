package com.edu.chat.domain.usecase

import com.edu.chat.domain.model.ChatMember
import com.edu.common.data.Result
import com.edu.common.domain.repository.IStudentsRepo
import com.edu.common.domain.repository.ITeachersRepo
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class SearchThroughAvailableUsersUseCase @Inject constructor(
    private val teacherRepo: ITeachersRepo,
    private val studentsRepo: IStudentsRepo
) {

    operator fun invoke(query: String): Flow<Result<List<ChatMember>>> {

        return flow {
            val allList = mutableListOf<ChatMember>()

            when (val studentsResult = studentsRepo.searchStudentsByQuery(query)) {
                is Result.Success -> {

                    allList.addAll(studentsResult.data?.map {
                        ChatMember(it.uid, it.name, it.avatarUrl)
                    } ?: emptyList())

                    when (val teachersResults =
                        teacherRepo.searchTeachers(query)) {
                        is Result.Success -> {
                            val teachers = teachersResults.data?.map {
                                ChatMember(
                                    it.uid
                                        ?: throw IllegalArgumentException("uid of teacher can't be null"),
                                    it.name
                                        ?: throw IllegalArgumentException("name of the teacher can't be null"),
                                    it.avatarUrl
                                )
                            } ?: emptyList()
                            allList.addAll(teachers)
                            emit(Result.Success(allList))
                        }
                        is Result.Error -> {
                            emit(Result.Error(teachersResults.data))
                        }
                    }
                }
                is Result.Error -> {
                    emit(Result.Error(studentsResult.data))
                }
            }
        }.catch {
            emit(Result.Error(it))
        }
    }
}