package com.edu.test.presentation.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.edu.common.utils.convertToTimeRepresentation
import com.edu.test.R
import com.edu.test.presentation.question.QuestionsViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay


@HiltWorker
class TestTimeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) :
    CoroutineWorker(context, params) {

    companion object {
        private const val DEFAULT_TIME = 10
        private const val CHANNEL_ID = "2"
        private const val NOTIFICATION_CHANNEL_NAME = "TIMER_NOTIFICATION"
        private const val NOTIFICATION_ID = 3
    }


    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private lateinit var notificationBuilder: NotificationCompat.Builder

    override suspend fun doWork(): Result {
        return try {
            val timeInMinutes = inputData.getInt(QuestionsViewModel.TIME_FOR_TEST, DEFAULT_TIME)
            val testId = inputData.getString(QuestionsViewModel.TEST_ID) ?: return Result.failure()
            setForeground(createForegroundInfo(getTimeText(timeInMinutes * 60)))
            var timeInSeconds = timeInMinutes * 60
            while (timeInSeconds >= 0) {
                setProgress(workDataOf("time" to timeInSeconds, "testId" to testId))
                timeInSeconds -= 1
                if (timeInSeconds != -1) {
                    delay(1000)
                    updateNotification(getTimeText(timeInSeconds))
                }
            }
            Result.success(workDataOf(QuestionsViewModel.TEST_ID to testId))
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val title = applicationContext.getString(R.string.notification_title)
        createNotificationChannel()

        notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        return ForegroundInfo(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun updateNotification(time: String) {
        notificationBuilder.setContentText(time)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getTimeText(time: Int): String {
        return time.convertToTimeRepresentation()
    }

    private fun createPendingIntent(): PendingIntent {
        return NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.navigation_test)
            .setDestination(R.id.questionsFragment)
            .createPendingIntent()
    }

}