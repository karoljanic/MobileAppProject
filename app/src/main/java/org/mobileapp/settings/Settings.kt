package org.mobileapp.settings

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.preference.PreferenceManager
import org.mobileapp.tracking.enums.ServiceStatus
import org.osmdroid.util.GeoPoint

object Settings {
    private lateinit var sharedPreferences: SharedPreferences

    const val TRACKER_SERVICE_NOTIFICATION_ID: Int = 1
    const val TRACKER_SERVICE_NOTIFICATION_CHANNEL: String = "TRACKER_SERVICE_NOTIFICATION_CHANNEL"
    const val TRACKER_SERVICE_NOTIFICATION_CHANNEL_NAME: String = "TRACKER_SERVICE_NOTIFICATION_CHANNEL_NAME"
    const val TRACKER_SERVICE_NOTIFICATION_CHANNEL_DESCRIPTION: String = "TRACKER_SERVICE_NOTIFICATION_DESCRIPTION"

    const val TEMPORARY_TRACK_FOLDER: String  = "temp-track"
    const val TRACKS_FOLDER: String = "tracks"
    const val GPX_FOLDER: String = "gpx"

    const val TEMPORARY_TRACK_FILE: String = "temp-track.json"
    const val TRACKS_FILE: String = "tracks.json"

    private const val DEFAULT_MAP_ZOOM = 10.0
    private const val MIN_MAP_ZOOM = 5.0
    private const val MAX_MAP_ZOOM = 20.0
    private const val DEFAULT_LOCATION_LATITUDE = 51.1078852    // Wroclaw latitude
    private const val DEFAULT_LOCATION_LONGITUDE = 17.0385376   // Wroclaw longitude
    private const val DEFAULT_LOCATION_PROVIDER = ""

    const val TIME_BETWEEN_WRITING_OF_SUCCESSIVE_TRACK_NODES = 1000L
    const val TIME_BETWEEN_SAVING_TRACK_TEMPORARY_FILES = 10000L
    const val LOCATION_AGE_THRESHOLD = 60000000000L
    const val LOCATION_ACCURACY_THRESHOLD = 30
    const val ACCURACY_MULTIPLIER = 300
    const val DISTANCE_THRESHOLD = 15.0F

    fun Context.initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    //fun registerPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
    //    sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    //}

    //fun unregisterPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
    //    sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    //}

    fun getDefaultMapZoom(): Double {
        return DEFAULT_MAP_ZOOM
    }

    fun getMinMapZoom(): Double {
        return MIN_MAP_ZOOM
    }

    fun getMaxMapZoom(): Double {
        return MAX_MAP_ZOOM
    }

    fun setMapZoom(mapZoom: Double) {
        saveDouble(SettingsKeys.MAP_ZOOM, mapZoom)
    }

    fun getMapZoom(): Double {
        return loadDouble(SettingsKeys.MAP_ZOOM, DEFAULT_MAP_ZOOM)
    }

    fun getDefaultLocation(): Location {
        val location: Location = Location(DEFAULT_LOCATION_PROVIDER)
        location.latitude = DEFAULT_LOCATION_LATITUDE
        location.longitude = DEFAULT_LOCATION_LONGITUDE

        return location
    }

    fun setLastLocation(location: Location) {
        saveDouble(SettingsKeys.LAST_SAVED_LOCATION_LATITUDE, location.latitude)
        saveDouble(SettingsKeys.LAST_SAVED_LOCATION_LONGITUDE, location.longitude)
    }

    fun getLastLocation(): Location {
        val location: Location = Location(DEFAULT_LOCATION_PROVIDER)
        location.latitude = loadDouble(SettingsKeys.LAST_SAVED_LOCATION_LATITUDE, DEFAULT_LOCATION_LATITUDE)
        location.longitude = loadDouble(SettingsKeys.LAST_SAVED_LOCATION_LONGITUDE, DEFAULT_LOCATION_LONGITUDE)

        return location
    }

    fun setCurrentServiceStatus(serviceStatus: ServiceStatus) {
        saveInt(SettingsKeys.CURRENT_SERVICE_STATUS, serviceStatus.ordinal)
    }

    fun getCurrentServiceStatus(): ServiceStatus {
        return ServiceStatus.fromOrdinal(loadInt(SettingsKeys.CURRENT_SERVICE_STATUS, ServiceStatus.IS_NOT_RUNNING.ordinal))
    }


    private fun saveDouble(key: String, value: Double) {
        with(sharedPreferences.edit()) {
            putString(key, value.toString())
            apply()
        }
    }

    private fun loadDouble(key: String, defaultValue: Double): Double {
        return sharedPreferences.getString(key, defaultValue.toString())!!.toDouble()
    }

    private fun saveInt(key: String, value: Int) {
        with(sharedPreferences.edit()) {
            putString(key, value.toString())
            apply()
        }
    }

    private fun loadInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getString(key, defaultValue.toString())!!.toInt()
    }
}