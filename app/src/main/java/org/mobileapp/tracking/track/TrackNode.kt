package org.mobileapp.tracking.track

import android.location.Location
import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize
import org.mobileapp.tracking.utils.LocationUtil
import java.util.Optional


@Parcelize
data class TrackNode(
    @Expose val provider: String,
    @Expose val latitude: Double,
    @Expose val longitude: Double,
    @Expose val altitude: Double,
    @Expose val accuracy: Float,
    @Expose val time: Long,
    @Expose val numberSatellites: Int
) : Parcelable {

    constructor(location: Location) : this(
        Optional.ofNullable(location.provider).orElse("unknown"),
        location.latitude,
        location.longitude,
        location.altitude,
        location.accuracy,
        location.time,
        LocationUtil.getNumberOfSatellites(location)
    )

    fun getLocation(): Location {
        val location: Location = Location(provider)

        location.latitude = latitude
        location.longitude = longitude
        location.altitude = altitude
        location.accuracy = accuracy
        location.time = time

        return location
    }
}