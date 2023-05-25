package org.mobileapp.viewmodel


import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.mobileapp.data.datastore.MapSettings
import org.mobileapp.domain.model.Track
import org.mobileapp.service.TrackerService

class MapViewModel : ViewModel() {
    private val _currentLocation = TrackerService.currentLocation
    private val _track = TrackerService.track
    private val _trackingTime = TrackerService.trackingTime
    private val _trackingDistance = TrackerService.trackingDistance

    val currentLocation: LiveData<Location>
        get() = _currentLocation

    val track: LiveData<Track>
        get() = _track

    val trackingTime: LiveData<Long>
        get() = _trackingTime

    val trackingDistance: LiveData<Double>
        get() = _trackingDistance

    fun mapZoom(context: Context): Double {
        return runBlocking { MapSettings(context).getZoom.first() }
    }
}