package org.mobileapp.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import org.mobileapp.MainActivity
import org.mobileapp.R
import org.mobileapp.data.configuration.TrackerServiceConfig
import org.mobileapp.service.enums.ServiceAction
import org.mobileapp.service.TrackerService
import org.mobileapp.service.enums.ServiceStatus

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
class TrackingNotificationBuilder(private val trackerService: TrackerService) {
    private val notificationManager: NotificationManager =
        trackerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val contentTitle: String = "Mobile App"
    private val contentText: String = "Application is tracking your position"


    private val stopActionPendingIntent = PendingIntent.getService(
        trackerService, 14, Intent(
            trackerService, TrackerService::class.java
        ).setAction(ServiceAction.STOP_SERVICE.str), PendingIntent.FLAG_IMMUTABLE
    )


    private val showActionPendingIntent: PendingIntent? =
        TaskStackBuilder.create(trackerService).run {
            addNextIntentWithParentStack(Intent(trackerService, MainActivity::class.java))
            getPendingIntent(10, PendingIntent.FLAG_IMMUTABLE)
        }

    private val showAction = NotificationCompat.Action(
        R.drawable.icon_show_24, "Show Notification", showActionPendingIntent
    )


    fun build(serviceStatus: ServiceStatus, duration: Long): Notification {
        if (shouldCreateNotificationChannel()) {
            createNotificationChannel()
        }

        val builder = NotificationCompat.Builder(
            trackerService, TrackerServiceConfig.NOTIFICATION_CHANNEL_ID
        )
        builder.setContentIntent(showActionPendingIntent)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setContentText(contentText)
        builder.setContentTitle(contentTitle)
        builder.addAction(showAction)
        builder.setLargeIcon(
            AppCompatResources.getDrawable(
                trackerService, R.drawable.ic_launcher_foreground
            )!!.toBitmap()
        )

        return builder.build()
    }


    private fun shouldCreateNotificationChannel() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() =
        notificationManager.getNotificationChannel(TrackerServiceConfig.NOTIFICATION_CHANNEL_ID) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            TrackerServiceConfig.NOTIFICATION_CHANNEL_ID,
            TrackerServiceConfig.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = TrackerServiceConfig.NOTIFICATION_CHANNEL_DESCRIPTION
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }
}