package com.example.smart_campus.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smart_campus.R
import com.example.smart_campus.screen.LoginScreen
import com.example.smart_campus.screen.Announcement_data.Announcement
import com.example.smart_campus.screen.Announcement_data.AnnouncementAppDataBase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM_SERVICE", "Message received from: ${remoteMessage.from}")

        // 1. Handle Data payload (to sync with local database)
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: ""
            val content = remoteMessage.data["content"] ?: ""
            val date = remoteMessage.data["date"] ?: ""

            if (title.isNotEmpty() && content.isNotEmpty()) {
                saveAnnouncementToDb(title, content, date)
            }
        }

        // 2. Handle Notification payload (for system tray)
        remoteMessage.notification?.let {
            Log.d("FCM_SERVICE", "Notification Title: ${it.title}")
            sendNotification(it.title ?: "New Announcement", it.body ?: "")
        } ?: run {
            // If there's no notification payload but there is data, show a notification manually
            if (remoteMessage.data.isNotEmpty()) {
                val title = remoteMessage.data["title"] ?: "New Announcement"
                val content = remoteMessage.data["content"] ?: ""
                sendNotification(title, content)
            }
        }
    }

    private fun saveAnnouncementToDb(title: String, content: String, date: String) {
        val database = AnnouncementAppDataBase.getDatabase(applicationContext)
        val dao = database.announcementDao()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val announcement = Announcement(
                    title = title,
                    content = content,
                    date = date,
                    isRead = false
                )
                dao.insertAnnouncement(announcement)
                Log.d("FCM_SERVICE", "Announcement saved to local DB: $title")
            } catch (e: Exception) {
                Log.e("FCM_SERVICE", "Failed to save announcement", e)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "New token generated: $token")
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, LoginScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "announcements_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Campus Announcements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new campus announcements"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}