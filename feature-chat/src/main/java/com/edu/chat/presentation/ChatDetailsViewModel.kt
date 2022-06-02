package com.edu.chat.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.edu.chat.data.worker.DownloadFileFromStorageWorker
import com.edu.chat.data.worker.UploadImageToStorageWorker
import com.edu.chat.domain.model.*
import com.edu.chat.domain.usecase.*
import com.edu.chat.presentation.model.ChatMessage
import com.edu.common.domain.Result
import com.edu.common.presentation.BaseViewModel
import com.edu.common.utils.parseDateForChat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
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
    private val getMessagesCountInChannel: GetMessagesCountUseCase,
    private val sendFileMessage: SendFileMessageUseCase,
    private val getUnsentMessagesFromDb: GetUnSentMessagesUseCase,
    private val workManager: WorkManager
) : BaseViewModel() {


    private var messagesMap = linkedMapOf<String, Message>()

    private val _chatMessages: MutableLiveData<List<ChatMessage>> = MutableLiveData()
    val chatMessages: LiveData<List<ChatMessage>> = _chatMessages

    private val _chatMemberInfo: MutableLiveData<ChatMember> = MutableLiveData()
    val chatMemberInfo: LiveData<ChatMember> = _chatMemberInfo

    private var channel: ChatChannel? = null

    private var otherUserId: String = ""

    private var unSentMessagesSize: Int = 0

    val uploadingOfFilesLiveData: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(
            TAG_UPLOADING_FILES
        )

    val downloadingOfFilesLiveData: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(
            TAG_DOWNLOADING_FILES
        )

    init {
        workManager.pruneWork()
    }


    companion object {
        private const val TAG_UPLOADING_FILES = "TAG_FOR_UPLOADING_OF_FILES"
        private const val TAG_DOWNLOADING_FILES = "TAG_FOR_DOWNLOADING_OF_FILES"
    }

    private var messagesCount: Int = 0

    private fun getProfileInfo(userId: String, role: String) {
        viewModelScope.launch {
            when (val response = chatMemberInfoUseCase(userId, role)) {
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
                    val channel = response.data ?: return@launch
                    this@ChatDetailsViewModel.channel = channel
                    getCountOfMessages()
                    coroutineScope {
                        getProfileInfo(otherUserId, channel.role)
                        listenToChatMessagesBeforeCurrentTimeStamp(otherUserId, channel.channelId)
                    }
                    listenToNewMessages(otherUserId, channel.channelId)
                    listenToUnSentMessages(otherUserId, channel.channelId)
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    fun listenToChatMessagesAgain() {
        /*
        channel?.channelId?.let {
            listenToChatMessagesBeforeCurrentTimeStamp(it)
            listenToNewMessages(otherUserId, it)
        }

         */
    }

    private fun listenToNewMessages(otherUserId: String, channelId: String) {
        viewModelScope.launch {
            newChatMessages(channelId).collect { newMessagesResponse ->
                when (newMessagesResponse) {
                    is Result.Success -> {
                        val response = newMessagesResponse.data ?: return@collect
                        if (response.isThereAnyChange) {
                            val messages = response.addedMessages.filter {
                                it.receiverFileUri == null && it.from == otherUserId && MessageTypeEnum.getTypeByValue(
                                    it.messageType
                                ) == MessageTypeEnum.Document
                            }
                            if (messages.isNotEmpty()) {
                                startDownloadingOfFiles(messages)
                            }
                            updateMessageMap(response)
                            _chatMessages.value = convertMessagesToChatMessage(
                                otherUserId,
                                messagesMap.size == messagesCount + unSentMessagesSize
                            )
                            markNewMessagesAsSeen(otherUserId, channelId)
                        }
                    }
                    is Result.Error -> {
                        _error.value = newMessagesResponse.data
                    }
                }
                hideLoader()
            }
        }
    }

    private fun listenToChatMessagesBeforeCurrentTimeStamp(otherUserId: String, channelId: String) {
        viewModelScope.launch {

            getChatMessages(channelId).collect { data ->
                when (data) {
                    is Result.Success -> {
                        val messages = data.data ?: return@collect
                        updateMessageMap(messages)

                        val msg = messages.addedMessages.filter {
                            it.receiverFileUri == null && MessageTypeEnum.getTypeByValue(it.messageType) == MessageTypeEnum.Document
                                    && it.from == otherUserId
                        }
                        if (msg.isNotEmpty()) {
                            startDownloadingOfFiles(msg)
                        }
                        if (messages.isThereAnyChange) {
                            _chatMessages.value = convertMessagesToChatMessage(
                                otherUserId,
                                messagesMap.size == messagesCount + unSentMessagesSize
                            )
                        }
                        markNewMessagesAsSeen(otherUserId, channelId)
                    }
                    is Result.Error -> {
                        _error.value = data.data
                    }
                }
            }
        }
    }

    fun getNextPageOfMessages(otherUserId: String) {
        if (messagesCount == 0 || messagesCount + unSentMessagesSize == messagesMap.size) {
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
                                    messagesMap.size == messagesCount + unSentMessagesSize
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
        val channel = channel ?: return
        viewModelScope.launch {
            getMessagesCountInChannel(channel.channelId).collect { result ->
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
            when (val response =
                markNewMessagesAsSeenUseCase(otherUserId, channelId, messages)) {
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

    fun sendTextMessage(
        message: String,
        to: String
    ) {
        channel ?: return
        viewModelScope.launch {
            val uid = UUID.randomUUID().toString()
            when (val response = newMessageUseCase(uid, message, to, channel!!)) {
                is Result.Success -> {
                    messagesMap[uid]?.let { message ->
                        messagesMap[uid] = message.copy(status = 1)
                        _chatMessages.value = convertMessagesToChatMessage(otherUserId, true)
                    }
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    fun sendFile(
        uri: String,
        to: String,
        messageTypeEnum: MessageTypeEnum
    ) {
        val channel = channel ?: return
        viewModelScope.launch {
            val uid = UUID.randomUUID().toString()
            when (val response = sendFileMessage(channel, uid, to, uri, messageTypeEnum)) {
                is Result.Success -> {
                    startFileUploadWork(uid, channel.channelId, uri)
                }
                is Result.Error -> {
                    _error.value = response.data
                }
            }
        }
    }

    private fun listenToUnSentMessages(otherUserId: String, channelId: String) {
        viewModelScope.launch {
            getUnsentMessagesFromDb(channelId).collect { messages ->
                unSentMessagesSize = messages.size
                val size = messagesMap.size
                messages.forEach { message ->
                    messagesMap[message.uid] = message
                }
                if (messagesMap.size != size && messages.isNotEmpty()) {
                    _chatMessages.value =
                        convertMessagesToChatMessage(
                            otherUserId,
                            messagesMap.size == messagesCount + messages.size
                        )
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

        messages.sortedBy {
            it.time
        }

        messages.forEachIndexed { index, message ->

            if (index == 0 && firstPage) {
                allMessages.add(ChatMessage.Header(messages[index].time.parseDateForChat()))
            }
            if (message.from == otherUserId) {
                allMessages.add(
                    ChatMessage.ReceivedMessage(
                        message.uid,
                        message,
                        false
                    )
                )
            } else {
                allMessages.add(
                    ChatMessage.SentMessage(
                        message.uid,
                        message,
                        false
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

    fun updateStateOfImageMessage(
        messageUid: String,
        isUploading: Boolean
    ) {
        if (_chatMessages.value == null) {
            return
        }
        val index = _chatMessages.value?.indexOfFirst {
            it.uid == messageUid
        } ?: -1
        if (index != -1) {
            val list = ArrayList(_chatMessages.value!!)
            val message = list[index] as ChatMessage.SentMessage
            list[index] = message.copy(imageIsUploaded = isUploading, showRetry = !isUploading)
            _chatMessages.value = list
        }
    }

    fun updateProgressOfDownloadingFile(
        messageUid: String,
        isDownloading: Boolean
    ) {
        if (_chatMessages.value == null) {
            return
        }
        val index = _chatMessages.value?.indexOfFirst {
            it.uid == messageUid
        } ?: -1

        if (index != -1) {
            val list = ArrayList(_chatMessages.value!!)
            val message = list[index] as ChatMessage.ReceivedMessage
            list[index] =
                message.copy(imageIsDownloaded = isDownloading)
            _chatMessages.value = list
        }
    }

    fun retryImageUploadWork(messageUid: String, uri: String) {
        val channel = channel ?: return
        startFileUploadWork(messageUid, channel.channelId, uri)
    }

    private fun startFileUploadWork(
        messageUid: String,
        channelID: String,
        uri: String
    ) {
        val request = OneTimeWorkRequestBuilder<UploadImageToStorageWorker>()
        request.setInputData(
            workDataOf(
                "uri" to uri,
                "messageUid" to messageUid,
                "channelId" to channelID
            )
        )
            .addTag(TAG_UPLOADING_FILES)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).setBackoffCriteria(BackoffPolicy.LINEAR, 0, TimeUnit.SECONDS)
        workManager.enqueueUniqueWork(messageUid, ExistingWorkPolicy.REPLACE, request.build())
    }

    private fun startDownloadingOfFiles(
        messages: List<Message>
    ) {
        val channelId = channel?.channelId ?: return
        messages.forEach { message ->
            val request = OneTimeWorkRequestBuilder<DownloadFileFromStorageWorker>()
                .setInputData(
                    workDataOf(
                        "fileName" to message.fileName,
                        "url" to message.messageUrl,
                        "messageUid" to message.uid,
                        "channelId" to  channelId
                    )
                ).addTag(TAG_DOWNLOADING_FILES).setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).build()
            workManager.enqueueUniqueWork(message.uid, ExistingWorkPolicy.KEEP, request)
        }
    }
}