package org.mobileapp.viewmodel


import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mobileapp.data.configuration.MapConfig
import org.mobileapp.data.datastore.MapSettings
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.TournamentStage
import org.mobileapp.domain.model.TournamentState
import org.mobileapp.domain.model.Track
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.domain.repository.TournamentRepository
import org.mobileapp.service.TrackerService
import org.osmdroid.util.GeoPoint
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val pRepo: ProfileRepository, private val tRepo: TournamentRepository
) : ViewModel() {
    val photoUrl get() = pRepo.photoUrl

    private val _track = TrackerService.track
    private val _trackingTime = TrackerService.trackingTime
    private val _trackingDistance = TrackerService.trackingDistance

    private val _centerLocation = mutableStateOf<GeoPoint?>(null)
    val centerLocation: State<GeoPoint?> = _centerLocation

    private val _userLocation = mutableStateOf<GeoPoint?>(null)
    val userLocation: State<GeoPoint?> = _userLocation

    private val _mapZoom = mutableStateOf<Double>(MapConfig.DEFAULT_MAP_ZOOM)
    val mapZoom: State<Double> = _mapZoom

    private val _tournaments = mutableStateOf(TournamentState())
    val tournaments: State<TournamentState> = _tournaments

    private val _stages = mutableListOf<TournamentStage>()
    val stages: MutableList<TournamentStage> = _stages

    private var mapCenterIsSet = false

    val track: LiveData<Track>
        get() = _track

    val trackingTime: LiveData<Long>
        get() = _trackingTime

    val trackingDistance: LiveData<Double>
        get() = _trackingDistance


    private val locationObserver: Observer<Location> = Observer { location ->
        if (_centerLocation.value == null) {
            _centerLocation.value = GeoPoint(location)
        } else if (!mapCenterIsSet) {
            mapCenterIsSet = true
            _centerLocation.value = GeoPoint(location)
        }

        _userLocation.value = GeoPoint(location)
    }

    init {
        TrackerService.currentLocation.observeForever(locationObserver)
        getTournaments()
    }

    override fun onCleared() {
        super.onCleared()

        TrackerService.currentLocation.removeObserver(locationObserver)
    }

    fun updateZoom(zoom: Double) {
        _mapZoom.value = zoom
    }

    fun updateCenterLocation(location: GeoPoint) {
        _centerLocation.value = location
    }

    fun showStages(stages: List<TournamentStage>) {
        _stages.clear()
        _stages.addAll(stages)
    }

    private fun getTournaments() = viewModelScope.launch {
        tRepo.getTournaments().collect { result ->
            when (result) {
                is Response.Success -> {
                    _tournaments.value =
                        TournamentState(data = result.data, isLoading = false, errorMsg = null)
                }

                is Response.Failure -> {
                    _tournaments.value =
                        TournamentState(data = null, isLoading = false, errorMsg = result.e.message)
                }

                is Response.Loading -> {
                    _tournaments.value =
                        TournamentState(data = null, isLoading = true, errorMsg = null)
                }
            }
        }
    }
}
