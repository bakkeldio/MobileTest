package com.edu.chat.data.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.edu.chat.domain.repository.IChatRepo
import com.edu.common.data.Result
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.io.File

@HiltWorker
class DownloadFileFromStorageWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted parameters: WorkerParameters,
    val chatRepo: IChatRepo,
    val db: FirebaseFirestore
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val messageUid = inputData.getString("messageUid") ?: return Result.failure()
        val fileName = inputData.getString("fileName") ?: return Result.failure()
        val downloadUrl = inputData.getString("url") ?: return Result.failure()
        val channelId = inputData.getString("channelId") ?: return Result.failure()

        val file = File(context.filesDir, fileName)

        setProgress(workDataOf("messageUid" to messageUid))
        return when (val response = chatRepo.downloadFileFromUrl(file, downloadUrl)) {
            is com.edu.common.data.Result.Success -> {
                try {
                    db.collection("channels").document(channelId).collection("messages")
                        .document(messageUid).update("receiverFileUri", file.toUri().toString())
                        .await()
                    Result.success(workDataOf("messageUid" to messageUid))
                } catch (e: Exception) {
                    Result.failure(workDataOf("exception" to e.localizedMessage))
                }
            }
            is com.edu.common.data.Result.Error -> {
                Result.failure(workDataOf("exception" to response.data?.localizedMessage))
            }
        }
    }
}