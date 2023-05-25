package org.mobileapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import androidx.core.content.ContextCompat
import org.mobileapp.data.configuration.TrackerServiceConfig
import kotlin.math.pow
import kotlin.math.sqrt

object LocationUtils {
    fun getNumberOfSatellites(location: Location): Int {
        val extras: Bundle = location.extras ?: return 0

        if (!extras.containsKey("satellites"))
            return 0

        return extras.getInt("satellites", 0)
    }

    fun isGpsEnabled(locationManager: LocationManager): Boolean {
        return if (locationManager.allProviders.contains(LocationManager.GPS_PROVIDER))
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        else
            false
    }

    fun isNetworkEnabled(locationManager: LocationManager): Boolean {
        return if (locationManager.allProviders.contains(LocationManager.NETWORK_PROVIDER))
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        else
            false
    }

    fun newLocationIsReliable(location: Location, currentBestLocation: Location?): Boolean {
        // https://developer.android.com/guide/topics/location/strategies.html#BestEstimate

        if (currentBestLocation == null) {
            return true
        }

        val timeDelta: Long = location.time - currentBestLocation.time
        val isSignificantlyNewer: Boolean = timeDelta > TrackerServiceConfig.SIGNIFICANT_TIME_DIFFERENCE
        val isSignificantlyOlder:Boolean = timeDelta < -TrackerServiceConfig.SIGNIFICANT_TIME_DIFFERENCE

        when {
            isSignificantlyNewer -> return true
            isSignificantlyOlder -> return false
        }

        val isNewer: Boolean = timeDelta > 0L
        val accuracyDelta: Float = location.accuracy - currentBestLocation.accuracy
        val isLessAccurate: Boolean = accuracyDelta > 0f
        val isMoreAccurate: Boolean = accuracyDelta < 0f
        val isSignificantlyLessAccurate: Boolean = accuracyDelta > 200f

        val isFromSameProvider: Boolean = location.provider == currentBestLocation.provider

        return when {
            isMoreAccurate -> true
            isNewer && !isLessAccurate -> true
            isNewer && !isSignificantlyLessAccurate && isFromSameProvider -> true
            else -> false
        }
    }

    fun isRecentEnough(location: Location): Boolean {
        val locationAge: Long = SystemClock.elapsedRealtimeNanos() - location.elapsedRealtimeNanos

        return locationAge < TrackerServiceConfig.LOCATION_AGE_THRESHOLD
    }


    fun isAccurateEnough(location: Location, locationAccuracyThreshold: Int): Boolean {
        val isAccurate: Boolean = when (location.provider) {
            LocationManager.GPS_PROVIDER -> location.accuracy < locationAccuracyThreshold
            else -> location.accuracy < locationAccuracyThreshold + 10
        }

        return isAccurate
    }

    fun isDifferentEnough(previousLocation: Location?, location: Location, accuracyMultiplier: Int): Boolean {
        if (previousLocation == null)
            return true

        val accuracy: Float = if (location.accuracy != 0.0f) location.accuracy else TrackerServiceConfig.DISTANCE_THRESHOLD
        val previousAccuracy: Float = if (previousLocation.accuracy != 0.0f) previousLocation.accuracy else TrackerServiceConfig.DISTANCE_THRESHOLD
        val accuracyDelta: Double = sqrt((accuracy.pow(2) + previousAccuracy.pow(2)).toDouble())
        val distance: Float = previousLocation.distanceTo(location)

        return distance > accuracyDelta * accuracyMultiplier
    }
}