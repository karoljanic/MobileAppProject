package org.mobileapp.data.configuration

object TrackerServiceConfig {
    const val NOTIFICATION_ID: Int = 1
    const val NOTIFICATION_CHANNEL_ID: String = "TRACKER_SERVICE_NOTIFICATION_CHANNEL_ID"
    const val NOTIFICATION_CHANNEL_NAME: String = "TRACKER_SERVICE_NOTIFICATION_CHANNEL_NAME"
    const val NOTIFICATION_CHANNEL_DESCRIPTION: String = "TRACKER_SERVICE_NOTIFICATION_DESCRIPTION"

    const val TIME_BETWEEN_WRITING_OF_SUCCESSIVE_TRACK_NODES = 1000L
    const val LOCATION_AGE_THRESHOLD = 60000000000L
    const val DISTANCE_THRESHOLD = 15.0F
    const val SIGNIFICANT_TIME_DIFFERENCE: Long = 120000L
}