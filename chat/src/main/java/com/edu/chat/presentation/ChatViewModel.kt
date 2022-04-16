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
import com.edu.common.data.Result
import com.edu.common.presentation.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val chatMembers: LiveData<List<ChatMemberItem>> = _chatMembers

    private val _nonMembers: MutableLiveData<List<ChatMember>> = MutableLiveData()
    val nonMembers: LiveData<List<ChatMember>> = _nonMembers

    private var chatMemberItems = mutableListOf<ChatMemberItem>()


    fun getChatMembers() {
        showLoader()
        viewModelScope.launch {
            getChatMembersUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        getChatsLastMessages(result.data ?: emptyList())
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
            getMessagesUpdatesForChatsUseCase().collect { messagesResult ->
                when (messagesResult) {
                    is Result.Success -> {
                        val chatUsers = members.map { member ->
                            val allMessages = messagesResult.data ?: emptyList()
                            val lastMessageWithThisStudent = allMessages.last {
                                it.chatUsers.contains(member.uid)
                            }

                            ChatMemberItem(
                                MessageTypeEnum.getTypeByValue(
                                    lastMessageWithThisStudent.messageType
                                ),
                                member.uid,
                                member.name,
                                member.avatarUrl,
                                if (lastMessageWithThisStudent.from == firebaseAuth.uid) MessageAuthorEnum.ME else MessageAuthorEnum.OTHER,
                                lastMessageWithThisStudent.status,
                                lastMessageWithThisStudent.time,
                                lastMessageWithThisStudent.message,
                                allMessages.filter {
                                    it.status == 1 && it.from == member.uid
                                }.count()
                            )

                        }
                        chatMemberItems.addAll(chatUsers)
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