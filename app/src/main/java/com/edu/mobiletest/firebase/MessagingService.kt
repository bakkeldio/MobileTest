package com.edu.mobiletest.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.edu.mobiletest.MainActivity
import com.edu.mobiletest.R
import com.edu.mobiletest.domain.repository.IProfileRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class MessagingService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        addTokenToFirestore(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        createNotificationChannel()
        showNotification(message.data)
    }

    private fun showNotification(dataPayload: Map<String, String>) {

        //create pending intent
        val intent = Intent(this, MainActivity::class.java)
            .setFlags(
                (Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        intent.putExtra("USER_ID", dataPayload["USER_ID"])
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            applicationContext,
            resources.getString(R.string.notification_channel_id)
        ).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(dataPayload["title"])
            .setSmallIcon(R.drawable.gcm_icon)
            .setContentText(dataPayload["body"])
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle())
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(123, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                resources.getString(R.string.notification_channel_id),
                "mobile-test",
                importance
            )
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun addTokenToFirestore(newRegistrationToken: String) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            MessagingServiceEntryPoint::class.java
        )
        val profileRepo = hiltEntryPoint.bindChatRepo()

        if (profileRepo.checkIfUserIsSignedIn()) {
            profileRepo.getFCMRegistrationTokens { tokens ->
                if (tokens.contains(newRegistrationToken)) {
                    return@getFCMRegistrationTokens
                }
                tokens.add(newRegistrationToken)
                profileRepo.updateFCMRegistrationTokens(tokens)
            }
        }
    }


    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MessagingServiceEntryPoint {
        fun bindChatRepo(): IProfileRepository
    }
}