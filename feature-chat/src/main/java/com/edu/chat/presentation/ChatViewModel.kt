package com.edu.chat.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.chat.domain.model.ChatMember
import com.edu.chat.domain.model.ChatMemberItem
import com.edu.chat.domain.model.MessageAuthorEnum
import com.edu.chat.domain.model.MessageTypeEnum
import com.edu.chat.domain.usecase.GetChatMembersUseCase
import com.edu.chat.domain.usecase.GetMessagesUpdatesForChatsUseCase
import com.edu.chat.domain.usecase.SearchThroughAvailableUsersUseCase
import com.edu.common.domain.Result
import com.edu.common.presentation.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatMembersUseCase: GetChatMembersUseCase,
    private val searchThroughAvailableUsers: SearchThroughAvailableUsersUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val getMessagesUpdatesForChatsUseCase: GetMessagesUpdatesForChatsUseCase
) :
    BaseViewModel() {


    private val _chatMembers: MutableLiveData<List<ChatMemberItem>> = MutableLiveData()
    var chatMembers: LiveData<List<ChatMemberItem>> = _chatMembers

    private val _nonMembers: MutableLiveData<List<ChatMember>> = MutableLiveData()
    val nonMembers: LiveData<List<ChatMember>> = _nonMembers

    private var chatMemberItems = listOf<ChatMemberItem>()


    fun getChatMembers() {
        showLoader()
        viewModelScope.launch {
            getChatMembersUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        getChatsLastMessages(result.data?.filter {
                            it.uid != firebaseAuth.uid
                        } ?: emptyList())
                    }
                    is Result.Error -> {
                        _error.value = result.data
                    }
                }
            }
        }
    }

    private fun getChatsLastMessages(members: List<ChatMember>) {
        viewModelScope.launch {
            getMessagesUpdatesForChatsUseCase().collectLatest { messagesResult ->
                when (messagesResult) {
                    is Result.Success -> {
                        val chatUsers = members.map { member ->
                            val allMessages = messagesResult.data ?: emptyList()
                            val lastMessageWithThisStudent = allMessages.lastOrNull {
                                it.chatUsers.contains(member.uid)
                            }

                            ChatMemberItem(
                                MessageTypeEnum.getTypeByValue(
                                    lastMessageWithThisStudent?.messageType
                                ),
                                member.uid,
                                member.name,
                                member.avatarUrl,
                                if (lastMessageWithThisStudent?.from == firebaseAuth.uid) MessageAuthorEnum.ME else MessageAuthorEnum.OTHER,
                                lastMessageWithThisStudent?.status ?: 0,
                                lastMessageWithThisStudent?.time,
                                message = when (MessageTypeEnum.getTypeByValue(
                                    lastMessageWithThisStudent?.messageType
                                )) {
                                    MessageTypeEnum.Text -> lastMessageWithThisStudent?.message
                                    MessageTypeEnum.Image -> "Фото"
                                    MessageTypeEnum.Document -> lastMessageWithThisStudent?.fileName
                                },
                                MessageTypeEnum.getTypeByValue(lastMessageWithThisStudent?.messageType),
                                allMessages.filter {
                                    it.status == 1 && it.from == member.uid
                                }.count()
                            )

                        }.sortedByDescending {
                            it.time
                        }
                        chatMemberItems = chatUsers
                        _chatMembers.value = chatUsers
                    }
                    is Result.Error -> {
                        _error.value = messagesResult.data
                    }
                }
                hideLoader()
            }
        }
    }

    fun resetChatMembers() {
        _chatMembers.value = chatMemberItems
        _nonMembers.value = emptyList()
    }

    fun searchThroughChatsMembers(query: String) {
        _chatMembers.value = chatMemberItems.filter {
            it.chatUserName.contains(query, ignoreCase = true)
        }
    }

    fun searchThroughNonChats(query: String) {
        viewModelScope.launch {
            searchThroughAvailableUsers(query).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val results = result.data?.toMutableList()
                        chatMemberItems.forEach {
                            results?.remove(results.find { chatMember ->
                                chatMember.uid == it.chatUserUid
                            })
                        }
                        results?.remove(results.find {
                            it.uid == firebaseAuth.uid
                        })
                        if (!results.isNullOrEmpty()) {
                            results.add(0, ChatMember())
                        }
                        _nonMembers.value = results
                    }
                    is Result.Error -> {
                        _error.value = result.data
                    }
                }
            }
        }
    }

}