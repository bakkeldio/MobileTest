package com.edu.chat.domain.usecase

import com.edu.chat.domain.model.ChatMember
import com.edu.chat.domain.repository.IChatRepo
import com.edu.common.domain.Result
import com.edu.common.domain.repository.IStudentsRepo
import com.edu.common.domain.repository.ITeachersRepo
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@ViewModelScoped
class GetChatMembersUseCase @Inject constructor(
    private val studentsRepo: IStudentsRepo,
    private val teachersRepo: ITeachersRepo,
    private val chatRepo: IChatRepo
) {

    suspend operator fun invoke(
    ): Flow<Result<List<ChatMember>>> {

        return flow {

            chatRepo.getEngagedChats().collect { result ->
                when (result) {
                    is Result.Success -> {

                        val teachersIds = result.data?.filter {
                            it.second.role == "teacher"
                        }?.map {
                            it.first
                        } ?: emptyList()
                        val studentsIds = result.data?.filter {
                            it.second.role == "student"
                        }?.map {
                            it.first
                        } ?: emptyList()

                        val allChatMembers: MutableList<ChatMember> = mutableListOf()
                        (if (studentsIds.isEmpty()) {
                            emptyList()
                        } else {
                            val ss =
                                (studentsRepo.getStudentsByIds(studentsIds) as? Result.Success)?.data

                            ss
                        })?.let {
                            allChatMembers.addAll(it.map { student ->
                                ChatMember(student.uid, student.name, student.avatarUrl)
                            })
                        }

                        allChatMembers.addAll((if (teachersIds.isEmpty()) {
                            emptyList()
                        } else {
                            (teachersRepo.getTeachersByIds(teachersIds) as? Result.Success)?.data
                                ?: emptyList()
                        }).map {
                            ChatMember(
                                it.uid
                                    ?: throw IllegalArgumentException("The id of teacher can't be null"),
                                it.name
                                    ?: throw IllegalArgumentException("The name of the teacher can't be null"),
                                it.avatarUrl
                            )
                        })
                        emit(Result.Success(allChatMembers))

                    }
                    is Result.Error -> {
                        emit(Result.Error(result.data))
                    }
                }
            }
        }.catch {
            Result.Error(it)
        }
    }
}
