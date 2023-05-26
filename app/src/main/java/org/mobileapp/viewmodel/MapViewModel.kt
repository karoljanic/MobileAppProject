package org.mobileapp.viewmodel


import android.content.Context
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.mobileapp.data.datastore.MapSettings
import org.mobileapp.domain.model.Track
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.service.TrackerService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


class MapViewModel : ViewModel() {
@HiltViewModel
class MapViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {
    val photoUrl get() = repo.photoUrl

    private val _currentLocation = TrackerService.currentLocation
    private val _track = TrackerService.track
    private val _trackingTime = TrackerService.trackingTime
    private val _trackingDistance = TrackerService.trackingDistance

    private val _centerLocation = mutableStateOf<Location?>(null)
    val centerLocation: State<Location?> = _centerLocation

    private val _userLocation = mutableStateOf<Location?>(null)
    val userLocation: State<Location?> = _userLocation

    private val _centerIsSet = mutableStateOf(false)
    val centerIsSet = _centerIsSet

    val track: LiveData<Track>
        get() = _track

    val trackingTime: LiveData<Long>
        get() = _trackingTime

    val trackingDistance: LiveData<Double>
        get() = _trackingDistance


    private val locationObserver: Observer<Location> = Observer { location ->
        if(_centerLocation.value == null) {
            _centerLocation.value = location
        }

        _userLocation.value = location
    }

    fun disableSettingCenter() {
        _centerIsSet.value = true
    }

    init {
        TrackerService.currentLocation.observeForever(locationObserver)
    }

    override fun onCleared() {
        super.onCleared()

        TrackerService.currentLocation.removeObserver(locationObserver)
    }
}