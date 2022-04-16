package com.edu.chat.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edu.chat.domain.model.*
import com.edu.chat.domain.usecase.*
import com.edu.chat.presentation.model.ChatMessage
import com.edu.common.data.Result
import com.edu.common.presentation.BaseViewModel
import com.edu.common.utils.parseDateForChat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class ChatDetailsViewModel @Inject constructor(
    private val getChatMessages: GetChatMessagesBeforeCurrentTimeUseCase,
    private val newMessageUseCase: SendNewMessageUseCase,
    private val getOrCreateChannelIdUseCase: GetOrCreateChannelIdUseCase,
    private val markNewMessagesAsSeenUseCase: MarkNewMessagesAsSeenUseCase,
    private val chatMemberInfoUseCase: GetChatMemberInfoUseCase,
    private val newChatMessages: GetNewChatMessagesUseCase,
    private val chatMessagesPagination: GetMessagesPaginationUseCase,
    private val getMessagesCountInChannel: GetMessagesCountUseCase
) : BaseViewModel() {


    private var messagesMap = linkedMapOf<String, Message>()

    private val _chatMessages: MutableLiveData<List<ChatMessage>> = MutableLiveData()
    val chatMessages: LiveData<List<ChatMessage>> = _chatMessages

    private val _chatMemberInfo: MutableLiveData<ChatMember> = MutableLiveData()
    val chatMemberInfo: LiveData<ChatMember> = _chatMemberInfo

    private var channel: ChatChannel? = null

    private var otherUserId: String = ""

    private var listenerActivated: Boolean = false

    private var messagesCount: Int = 0

    fun getProfileInfo(userId: String) {
        viewModelScope.launch {
            when (val response = chatMemberInfoUseCase(userId)) {
                is Result.Success -> {
                    _chatMemberInfo.value = response.data
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    fun getOrCreateChannel(otherUserId: String) {
        showLoader()
        this.otherUserId = otherUserId
        viewModelScope.launch {
            when (val response = getOrCreateChannelIdUseCase(otherUserId)) {
                is Result.Success -> {
                    channel = response.data
                    getCountOfMessages()
                    coroutineScope {
                        listenToChatMessagesBeforeCurrentTimeStamp(response.data!!.channelId)
                    }
                    listenToNewMessages(otherUserId, response.data!!.channelId)
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    fun listenToChatMessagesAgain() {
        if (!listenerActivated) {
            channel?.channelId?.let {
                //listenToChatMessagesBeforeCurrentTimeStamp(it)
                listenToNewMessages(otherUserId, it)
            }
        }
    }

    private fun listenToNewMessages(otherUserId: String, channelId: String) {
        viewModelScope.launch {
            newChatMessages(channelId).collect { newMessagesResponse ->
                when (newMessagesResponse) {
                    is Result.Success -> {
                        newMessagesResponse.data?.let {
                            updateMessageMap(
                                it
                            )
                        }
                        _chatMessages.value = convertMessagesToChatMessage(
                            otherUserId,
                            messagesMap.size == messagesCount
                        )
                        markNewMessagesAsSeen(otherUserId, channelId)
                    }
                    is Result.Error -> {
                        _error.value = newMessagesResponse.data
                    }
                }
                listenerActivated = false
                hideLoader()
            }
        }
    }

    private fun listenToChatMessagesBeforeCurrentTimeStamp(channelId: String) {
        listenerActivated = true
        viewModelScope.launch {

            getChatMessages(channelId).collect { data ->
                when (data) {
                    is Result.Success -> {
                        data.data?.let {
                            updateMessageMap(
                                it
                            )
                        }
                        if (!listenerActivated) {
                            _chatMessages.value = convertMessagesToChatMessage(
                                otherUserId,
                                messagesMap.size == messagesCount
                            )
                        }
                    }
                    is Result.Error -> {
                        _error.value = data.data
                    }
                }
            }
        }
    }

    fun getNextPageOfMessages(otherUserId: String) {
        if (messagesCount == 0 || messagesCount == messagesMap.size) {
            return
        }
        viewModelScope.launch {
            channel?.channelId?.let {
                chatMessagesPagination(it).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val linkedMap = linkedMapOf<String, Message>()
                            result.data?.addedMessages?.forEach { message ->
                                linkedMap[message.uid] = message
                            }
                            messagesMap.forEach { map ->
                                linkedMap[map.key] = map.value
                            }
                            result.data?.modifiedMessages?.forEach { message ->
                                linkedMap[message.uid] = message
                            }
                            result.data?.removedMessages?.forEach { message ->
                                linkedMap.remove(message.uid)
                            }
                            messagesMap = linkedMap
                            _chatMessages.value =
                                convertMessagesToChatMessage(
                                    otherUserId = otherUserId,
                                    messagesMap.size == messagesCount
                                )
                        }
                        is Result.Error -> {
                            _error.value = result.data
                        }
                    }
                }

            }
        }
    }

    private fun getCountOfMessages() {
        if (channel?.channelId == null) {
            return
        }
        viewModelScope.launch {
            getMessagesCountInChannel(channel!!.channelId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        messagesCount = (result.data ?: 0).toInt()
                    }
                    is Result.Error -> {
                        _error.value = result.data
                    }
                }
            }
        }
    }

    private fun markNewMessagesAsSeen(otherUserId: String, channelId: String) {
        viewModelScope.launch {

            val messages = _chatMessages.value?.map {
                it.message
            } ?: emptyList()
            when (val response = markNewMessagesAsSeenUseCase(otherUserId, channelId, messages)) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    private fun areTwoDatesDaysEqual(firstDate: Date, secondDate: Date): Boolean {
        val firstMessageCalendar = Calendar.getInstance()
        firstMessageCalendar.time = firstDate
        val secondMessageCalendar = Calendar.getInstance()
        secondMessageCalendar.time = secondDate
        return firstMessageCalendar[Calendar.YEAR] == secondMessageCalendar[Calendar.YEAR] &&
                firstMessageCalendar[Calendar.MONTH] == secondMessageCalendar[Calendar.MONTH] &&
                firstMessageCalendar[Calendar.DAY_OF_MONTH] == secondMessageCalendar[Calendar.DAY_OF_MONTH]
    }

    fun sendMessage(message: String, to: String, messageType: MessageTypeEnum) {
        viewModelScope.launch {
            val uid = UUID.randomUUID().toString()
            when (val response = newMessageUseCase(uid, message, messageType, to, channel!!)) {
                is Result.Success -> {
                    messagesMap[uid]?.let {
                        messagesMap[uid] = it.copy(status = 1)
                        _chatMessages.value = convertMessagesToChatMessage(otherUserId, false)
                    }
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    private fun convertMessagesToChatMessage(
        otherUserId: String,
        firstPage: Boolean
    ): List<ChatMessage> {
        val allMessages = mutableListOf<ChatMessage>()


        val messages = messagesMap.values.toMutableList()
        messages.forEachIndexed { index, message ->

            if (index == 0 && firstPage) {
                allMessages.add(ChatMessage.Header(messages[index].time.parseDateForChat()))
            }
            if (message.from == otherUserId) {
                allMessages.add(
                    ChatMessage.ReceivedMessage(
                        message.uid,
                        message
                    )
                )
            } else {
                allMessages.add(
                    ChatMessage.SentMessage(
                        message.uid,
                        message
                    )
                )
            }
            if (index < messagesMap.size - 1 && !areTwoDatesDaysEqual(
                    message.time,
                    messages[index + 1].time
                )
            ) {
                allMessages.add(ChatMessage.Header(messages[index + 1].time.parseDateForChat()))
            }
        }
        return allMessages
    }

    private fun updateMessageMap(
        messageComposed: MessageComposed
    ) {

        messageComposed.addedMessages.forEach {
            messagesMap[it.uid] = it
        }
        messageComposed.modifiedMessages.forEach {
            messagesMap[it.uid] = it
        }

        messageComposed.removedMessages.forEach {
            messagesMap.remove(it.uid)
        }
    }

}