package com.theusmadev.coronareminder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.theusmadev.coronareminder.BuildConfig
import com.theusmadev.coronareminder.R
import com.theusmadev.coronareminder.ui.coronareminders.ReminderDetailActivity

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

fun sendNotification(context: Context, notificationTitle: String, timestamp: Long) {
    val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val intent = ReminderDetailActivity.newIntent(context.applicationContext, notificationTitle, timestamp)

    val stackBuilder = TaskStackBuilder.create(context)
            .addParentStack(ReminderDetailActivity::class.java)
            .addNextIntent(intent)
    val notificationPendingIntent = stackBuilder
            .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificationTitle)
            .setContentText("Click to check recommendations against Covid-19")
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .build()

    notificationManager.notify(getUniqueId(), notification)
}

fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())