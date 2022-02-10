package com.example.test.presentation.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.example.test.R
import com.example.test.presentation.question.QuestionsViewModel
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
            val testId = inputData.getString(QuestionsViewModel.TEST_ID)
            setForeground(createForegroundInfo(getTimeText(timeInMinutes)))
            var timeInM = timeInMinutes
            while (timeInM >= 0) {
                setProgress(workDataOf("time" to timeInM, "testId" to testId))
                timeInM -= 1
                if (timeInM != -1) {
                    delay(60_000)
                    updateNotification(getTimeText(timeInM))
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
        return applicationContext.run {
            val minutesPlurals =
                resources.getQuantityString(R.plurals.minutes_plural, time, time)
            resources.getString(R.string.test_will_end_in).format(minutesPlurals)
        }
    }

    private fun createPendingIntent(): PendingIntent {
        return NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.navigation_test)
            .setDestination(R.id.questionsFragment)
            .createPendingIntent()
    }

}