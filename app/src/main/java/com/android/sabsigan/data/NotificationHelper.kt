package com.android.sabsigan.data

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.sabsigan.R

class NotificationHelper(base: Context?): ContextWrapper(base) {
    private val channelID: String = "channelID"
    private val channelNm: String = "channelName"

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "My Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, channelNm, importance).apply {
                description = descriptionText
            }

            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            getManager().createNotificationChannel(channel)
        }
    }

    fun getManager(): NotificationManager {
        return  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun getChannelNotification(title: String, content: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.drawable.baseline_add_24)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true) // 사용자가 알림을 탭하면 자동으로 알림을 제거
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }
}