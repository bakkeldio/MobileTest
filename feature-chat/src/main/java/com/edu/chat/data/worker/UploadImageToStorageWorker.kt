package com.edu.chat.data.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.edu.chat.data.db.dao.MessageDao
import com.edu.chat.domain.model.Message
import com.edu.chat.domain.model.MessageTypeEnum
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.io.File

@HiltWorker
class UploadImageToStorageWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val messageDao: MessageDao
) : CoroutineWorker(context, parameters) {


    override suspend fun doWork(): Result {
        val uri = Uri.parse(inputData.getString("uri"))
        val channelId = inputData.getString("channelId") ?: return Result.failure()
        val messageUid = inputData.getString("messageUid") ?: return Result.failure()
        setProgress(workDataOf("messageUid" to messageUid))

        return try {

            val url = storage.reference
                .child("channelFiles/${channelId}_${messageUid}")
                .putFile(uri)
                .await()
                .storage.downloadUrl.await().toString()

            val message =
                messageDao.getMessageById(messageUid).firstOrNull() ?: return Result.failure()

            messageDao.deleteMessageById(messageUid)


            db.runBatch { batch ->
                batch.set(
                    db.collection("channels")
                        .document(channelId)
                        .collection("messages")
                        .document(messageUid), Message(
                        message.uid,
                        message.time,
                        message.messageType,
                        1,
                        null,
                        url,
                        message.messageUri,
                        null,
                        message.fileName,
                        message.fileExtension,
                        message.fileSize,
                        message.chatUsers,
                        message.to,
                        message.from,
                        message.senderName,
                        message.recipientRole
                    )
                )
                batch.update(
                    db.collection("channels").document(channelId),
                    "messagesCount",
                    FieldValue.increment(1)
                )
            }.await()

            if (MessageTypeEnum.getTypeByValue(message.messageType) == MessageTypeEnum.Image) {
                uri.path?.let {
                    File(it).delete()
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure(workDataOf("messageUid" to messageUid))
        }
    }

}