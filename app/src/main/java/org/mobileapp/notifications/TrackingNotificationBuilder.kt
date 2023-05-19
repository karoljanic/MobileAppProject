package org.mobileapp.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import org.mobileapp.MainActivity
import org.mobileapp.R
import org.mobileapp.settings.Settings
import org.mobileapp.tracking.config.Configuration
import org.mobileapp.tracking.enums.ServiceAction
import org.mobileapp.tracking.enums.ServiceStatus
import org.mobileapp.tracking.service.TrackerService

class TrackingNotificationBuilder(private val trackerService: TrackerService) {
    private val notificationManager: NotificationManager = trackerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val contentTitle: String = "Some Title"
    private val contentText: String = "Some Interesting Content"

    private val stopActionPendingIntent = PendingIntent.getService(
        trackerService, 14,
        Intent(trackerService, TrackerService::class.java).setAction(ServiceAction.ACTION_STOP.string), PendingIntent.FLAG_IMMUTABLE
    )

    private val resumeActionPendingIntent = PendingIntent.getService(
        trackerService, 16,
        Intent(trackerService, TrackerService::class.java).setAction(ServiceAction.ACTION_RESUME.string), PendingIntent.FLAG_IMMUTABLE
    )

    private val showActionPendingIntent: PendingIntent? = TaskStackBuilder.create(trackerService).run {
        addNextIntentWithParentStack(Intent(trackerService, MainActivity::class.java))
        getPendingIntent(10, PendingIntent.FLAG_IMMUTABLE)
    }


    private val stopAction = NotificationCompat.Action(
        R.drawable.icon_stop_24,
        "Pause Notification",
        stopActionPendingIntent
    )

    private val resumeAction = NotificationCompat.Action(
        R.drawable.icon_restart_24,
        "Resume Notification",
        resumeActionPendingIntent
    )

    private val showAction = NotificationCompat.Action(
        R.drawable.icon_show_24,
        "Show Notification",
        showActionPendingIntent
    )


    fun build(serviceStatus: ServiceStatus, duration: Long): Notification {
        if (shouldCreateNotificationChannel()) {
            createNotificationChannel()
        }

        val builder = NotificationCompat.Builder(trackerService, Configuration.TRACKER_SERVICE_NOTIFICATION_CHANNEL)
        builder.setContentIntent(showActionPendingIntent)
        builder.setSmallIcon(R.drawable.icon_notifications_active_24)
        builder.setContentText(contentText)

        when (serviceStatus) {
            ServiceStatus.IS_RUNNING -> {
                builder.setContentTitle(contentTitle)
                builder.addAction(stopAction)
                builder.setLargeIcon(AppCompatResources.getDrawable(trackerService, R.drawable.icon_notifications_active_24)!!.toBitmap())
            }
            else -> {
                builder.setContentTitle(contentTitle)
                builder.addAction(resumeAction)
                builder.addAction(showAction)
                builder.setLargeIcon(AppCompatResources.getDrawable(trackerService, R.drawable.icon_notifications_active_24)!!.toBitmap())
            }
        }

        return builder.build()
    }


    private fun shouldCreateNotificationChannel() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() = notificationManager.getNotificationChannel(Configuration.TRACKER_SERVICE_NOTIFICATION_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            Configuration.TRACKER_SERVICE_NOTIFICATION_CHANNEL,
            Configuration.TRACKER_SERVICE_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
            .apply { description = Configuration.TRACKER_SERVICE_NOTIFICATION_CHANNEL_DESCRIPTION }
        notificationManager.createNotificationChannel(notificationChannel)
    }
}