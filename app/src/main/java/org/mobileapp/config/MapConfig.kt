package org.mobileapp.config

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.osmdroid.util.GeoPoint


object MapConfig {
    private lateinit var sharedPreferences: SharedPreferences

    private const val DEFAULT_MAP_ZOOM = 10.0
    private const val MIN_ZOOM = 5.0
    private const val MAX_ZOOM = 20.0
    private const val DEFAULT_LOCATION_LATITUDE = 51.1078852    // Wroclaw latitude
    private const val DEFAULT_LOCATION_LONGITUDE = 17.0385376   // Wroclaw longitude

    fun Context.initPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    fun getDefaultMapZoom(): Double {
        return DEFAULT_MAP_ZOOM
    }

    fun getMinZoom(): Double {
        return MIN_ZOOM
    }

    fun getMaxZoom(): Double {
        return MAX_ZOOM
    }

    fun setZoom(zoom: Double) {
        saveDouble(ConfigKeys.MAP_ZOOM, zoom)
    }

    fun getZoom(): Double {
        return loadDouble(ConfigKeys.MAP_ZOOM, DEFAULT_MAP_ZOOM)
    }

    fun getDefaultLocation(): GeoPoint {
        return GeoPoint(DEFAULT_LOCATION_LATITUDE, DEFAULT_LOCATION_LONGITUDE)
    }

    fun setLocation(latitude: Double, longitude: Double) {
        saveDouble(ConfigKeys.LOCATION_LATITUDE, latitude)
        saveDouble(ConfigKeys.LOCATION_LONGITUDE, longitude)
    }

    fun getLocation(): GeoPoint {
        return GeoPoint(
            loadDouble(ConfigKeys.LOCATION_LATITUDE, DEFAULT_LOCATION_LATITUDE),
            loadDouble(ConfigKeys.LOCATION_LONGITUDE, DEFAULT_LOCATION_LONGITUDE)
        )
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
}