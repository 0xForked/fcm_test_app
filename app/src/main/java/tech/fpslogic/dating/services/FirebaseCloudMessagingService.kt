package tech.fpslogic.dating.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tech.fpslogic.dating.MainActivity
import tech.fpslogic.dating.R

class FirebaseCloudMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        message.data.isNotEmpty().let {
            if (!message.data.isNullOrEmpty()) {
                sendNotification(message = message.data)
            }
        }

        message.notification?.let {
            sendNotification(
                message = message.data,
                title = it.title.toString(),
                body = it.body.toString()
            )
        }
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(
        message: Map<String, String>,
        title: String = "eAbsentBSG",
        body: String = "New Notification"
    ) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            intent, PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(body))
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // Since android Oreo notification channel is needed.
        // https://developer.android.com/training/notify-user/build-notification#Priority
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())

        Log.d("FCM_MESSAGE", message.toString())
        Log.d("FCM_TITLE", title)
        Log.d("FCM_BODY", body)
    }

    override fun onNewToken(token: String) {
        Log.d("FCM_TOKEN", token)
    }
}