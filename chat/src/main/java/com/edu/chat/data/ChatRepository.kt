package com.edu.chat.data

import android.content.SharedPreferences
import com.edu.chat.domain.model.*
import com.edu.chat.domain.repository.IChatRepo
import com.edu.common.data.Result
import com.edu.common.domain.model.StudentInfoDomain
import com.edu.common.domain.model.TeacherProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@ActivityRetainedScoped
class ChatRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) : IChatRepo {

    private var lastDocumentDate: Date? = null

    companion object {
        private const val PAGE_SIZE: Long = 25
    }


    override suspend fun getOrCreateChannel(otherUserId: String): Result<ChatChannel> {
        return try {
            val isUserAdmin = sharedPreferences.getBoolean("isUserAdmin", false)
            val engagedChannelRef = if (!isUserAdmin) {
                db.collection("students")
                    .document(auth.uid!!)
                    .collection("engagedChatChannels")
                    .document(otherUserId)
            } else {
                db.collection("teachers")
                    .document(auth.uid!!)
                    .collection("engagedChatChannels")
                    .document(otherUserId)
            }


            val result = engagedChannelRef.get().await()

            if (result.exists()) {
                return Result.Success(result.toObject(ChatChannel::class.java))
            }

            val channelResult = db.collection("channels").add(
                hashMapOf("userIds" to listOf(auth.uid!!, otherUserId))
            ).await()


            val otherUserIsTeacher =
                db.collection("teachers").document(otherUserId).get().await().exists()
            val channel = ChatChannel(
                channelResult.id,
                if (otherUserIsTeacher) "teacher" else "student"
            )
            engagedChannelRef.set(
                channel
            ).await()


            if (otherUserIsTeacher) {
                db.collection("teachers").document(otherUserId).collection("engagedChatChannels")
                    .document(auth.uid!!)
                    .set(ChatChannel(channelResult.id, if (isUserAdmin) "teacher" else "student"))
                    .await()
            } else {
                db.collection("students")
                    .document(otherUserId).collection("engagedChatChannels")
                    .document(auth.uid!!)
                    .set(ChatChannel(channelResult.id, if (isUserAdmin) "teacher" else "student"))
                    .await()
            }

            Result.Success(channel)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getMessagesCountInChannel(channelId: String): Flow<Result<Long>> {
        return callbackFlow {
            val listener = db.collection("channels").document(channelId).addSnapshotListener { value, error ->
                if (error != null){
                    close(error)
                    return@addSnapshotListener
                }
                val messageCount = value?.getLong("messagesCount") ?: 0
                trySend(Result.Success(messageCount))
            }

            awaitClose {
                listener.remove()
            }
        }
    }

    override fun listenToNewMessagesFromChannels(): Flow<Result<List<Message>>> {
        return callbackFlow {
            val listener =
                db.collectionGroup("messages").whereArrayContains("chatUsers", auth.uid!!)
                    .orderBy("time")
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        val data = value?.toObjects(Message::class.java)
                        trySend(Result.Success(data))
                    }
            awaitClose {
                listener.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getEngagedChats(): Flow<Result<List<Pair<String, ChatChannel>>>> {
        return callbackFlow {
            val isUserAdmin = sharedPreferences.getBoolean("isUserAdmin", false)
            val refToEngagedChats = if (isUserAdmin) {
                db.collection("teachers").document(auth.uid!!)
                    .collection("engagedChatChannels")
            } else {
                db.collection("students").document(auth.uid!!).collection("engagedChatChannels")
            }
            val listener = refToEngagedChats
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val channels = value?.map { snapshot ->
                        val channel = snapshot.toObject(ChatChannel::class.java)
                        Pair(snapshot.id, channel)
                    } ?: emptyList()
                    trySend(Result.Success(channels))
                }

            awaitClose {
                listener.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getMessagesOfChatChannelBeforeCurrentTimeStamp(channelId: String): Flow<Result<MessageComposed>> {
        return callbackFlow {
            val listener =
                db.collection("channels")
                    .document(channelId)
                    .collection("messages")
                    .orderBy("time")
                    .endBefore(Calendar.getInstance().time)
                    .limitToLast(PAGE_SIZE)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        value?.documentChanges?.firstOrNull()?.let {
                            if (it.type == DocumentChange.Type.ADDED){
                                lastDocumentDate = it.document.toObject(Message::class.java).time
                            }
                        }

                        trySend(Result.Success(getMessageComposed(value)))
                    }

            awaitClose {
                listener.remove()
            }
        }
    }

    override fun getPreviousMessagesOfChat(channelId: String): Flow<Result<MessageComposed>> {
        return callbackFlow {
            val listener = db.collection("channels")
                .document(channelId)
                .collection("messages")
                .orderBy("time")
                .endBefore(lastDocumentDate)
                .limitToLast(PAGE_SIZE).addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    value?.documentChanges?.firstOrNull()?.let {
                        if (it.type == DocumentChange.Type.ADDED) {
                            lastDocumentDate = it.document.toObject(Message::class.java).time
                        }
                    }
                    trySend(Result.Success(getMessageComposed(value)))
                }
            awaitClose {
                listener.remove()
            }
        }
    }

    override fun getNewMessagesOfChat(channelId: String): Flow<Result<MessageComposed>> {
        return callbackFlow {
            val listener = db.collection("channels")
                .document(channelId)
                .collection("messages")
                .orderBy("time")
                .startAfter(Calendar.getInstance().time)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    trySend(Result.Success(getMessageComposed(value)))
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    override suspend fun sendNewMessageToUser(
        channel: ChatChannel,
        userId: String,
        messageUid: String,
        message: String,
        messageType: MessageTypeEnum
    ): Result<Unit> {
        return try {

            db.runBatch { batch ->
                batch.set(
                    db.collection("channels").document(channel.channelId).collection("messages")
                        .document(messageUid), Message(
                        messageUid,
                        Calendar.getInstance().time,
                        messageType.value,
                        1,
                        message,
                        listOf(userId, auth.uid!!),
                        userId,
                        auth.uid!!,
                        sharedPreferences.getString("currentUserName", "")!!,
                        channel.role
                    )
                )
                batch.update(
                    db.collection("channels").document(channel.channelId),
                    "messagesCount",
                    FieldValue.increment(1)
                )
            }.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun markNewMessagesAsSeen(channelId: String, ids: List<String>): Result<Unit> {
        return try {
            val ref = db.collection("channels").document(channelId).collection("messages")

            db.runBatch { batch ->
                ids.forEach {
                    batch.update(ref.document(it), "status", 2)
                }
            }.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getChatMemberInfo(userId: String, role: String): Result<ChatMember> {
        return try {
            if (role == "teacher") {
                val snapshot = db.collection("teachers").document(userId).get().await()
                val teacher = snapshot.toObject(TeacherProfile::class.java)
                teacher ?: throw IllegalArgumentException("teacher model can't be null")
                Result.Success(ChatMember(userId, teacher.name!!, teacher.avatarUrl))
            } else {
                val snapshot = db.collection("students").document(userId).get().await()
                val student = snapshot.toObject(StudentInfoDomain::class.java)
                student ?: throw IllegalArgumentException("student model can't be null")
                Result.Success(ChatMember(userId, student.name, student.avatarUrl))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun getMessageComposed(value: QuerySnapshot?): MessageComposed {
        val listOfModifiedMessages = mutableListOf<Message>()
        val listOfRemovedMessages = mutableListOf<Message>()
        val listOfAddedMessages = mutableListOf<Message>()
        value?.documentChanges?.forEachIndexed { _, documentChange ->
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    listOfAddedMessages.add(
                        documentChange.document.toObject(Message::class.java).let {
                            it.copy(
                                status = if (it.status == 2)
                                    MessageStatusEnum.SEEN.value
                                else if (!documentChange.document.metadata.hasPendingWrites()) MessageStatusEnum.SENT.value
                                else MessageStatusEnum.NOT_SENT.value
                            )
                        })
                }
                DocumentChange.Type.MODIFIED -> {
                    listOfModifiedMessages.add(
                        documentChange.document.toObject(Message::class.java).let {
                            it.copy(
                                status = if (it.status == 2)
                                    MessageStatusEnum.SEEN.value
                                else if (!documentChange.document.metadata.hasPendingWrites()) MessageStatusEnum.SENT.value
                                else MessageStatusEnum.NOT_SENT.value
                            )
                        })
                }
                DocumentChange.Type.REMOVED -> {
                    listOfRemovedMessages.add(documentChange.document.toObject(Message::class.java))
                }
            }
        }
        return MessageComposed(listOfRemovedMessages, listOfModifiedMessages, listOfAddedMessages)
    }

}