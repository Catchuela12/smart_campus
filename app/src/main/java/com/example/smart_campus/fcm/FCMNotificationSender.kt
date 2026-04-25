package com.example.smart_campus.fcm

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class to send FCM notifications using HTTP v1 API directly from the app.
 * Note: In production, this should be handled by a secure backend.
 */
object FCMNotificationSender {

    // Matches the actual filename in your assets folder
    private const val SERVICE_ACCOUNT_FILE = "service-account.json"
    private const val FCM_V1_URL = "https://fcm.googleapis.com/v1/projects/%s/messages:send"

    suspend fun sendAnnouncementNotification(context: Context, projectId: String, title: String, body: String) {
        withContext(Dispatchers.IO) {
            try {
                val accessToken = getAccessToken(context)
                val urlString = String.format(FCM_V1_URL, projectId)
                
                val root = JSONObject()
                val message = JSONObject()
                
                // 1. Notification payload (for system tray)
                val notification = JSONObject()
                notification.put("title", title)
                notification.put("body", body)
                
                // 2. Data payload (for app to process in background/foreground)
                val data = JSONObject()
                data.put("title", title)
                data.put("content", body)
                // Sending current date so student app knows when it was posted
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                data.put("date", dateStr)
                
                message.put("topic", "announcements")
                message.put("notification", notification)
                message.put("data", data)
                root.put("message", message)

                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Authorization", "Bearer $accessToken")
                conn.doOutput = true

                val os = conn.outputStream
                os.write(root.toString().toByteArray())
                os.flush()
                os.close()

                val responseCode = conn.responseCode
                if (responseCode == 200) {
                    Log.d("FCM_SENDER", "Notification sent successfully")
                } else {
                    val error = conn.errorStream.bufferedReader().readText()
                    Log.e("FCM_SENDER", "Error response: $error")
                }
                conn.disconnect()
            } catch (e: Exception) {
                Log.e("FCM_SENDER", "Error sending notification", e)
            }
        }
    }

    private fun getAccessToken(context: Context): String {
        val inputStream = context.assets.open(SERVICE_ACCOUNT_FILE)
        val googleCredentials = GoogleCredentials.fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        googleCredentials.refreshIfExpired()
        return googleCredentials.accessToken.tokenValue
    }
}