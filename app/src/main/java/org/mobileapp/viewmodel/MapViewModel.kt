package org.mobileapp.viewmodel


import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.mobileapp.data.configuration.MapConfig
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.StageState
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.model.TournamentStage
import org.mobileapp.domain.model.TournamentState
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.domain.repository.TournamentRepository
import org.mobileapp.service.TrackerService
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val pRepo: ProfileRepository, private val tRepo: TournamentRepository
) : ViewModel() {
    val photoUrl get() = pRepo.photoUrl

    private val _centerLocation = mutableStateOf<GeoPoint?>(null)
    val centerLocation: State<GeoPoint?> = _centerLocation

    private val _userLocation = mutableStateOf<GeoPoint?>(null)
    val userLocation: State<GeoPoint?> = _userLocation

    private val _userOverlay = mutableStateOf<ItemizedOverlay<OverlayItem>?>(null)
    val userOverlay = _userOverlay

    private val _mapZoom = mutableStateOf<Double>(MapConfig.DEFAULT_MAP_ZOOM)
    val mapZoom: State<Double> = _mapZoom

    private val _tournaments = mutableStateOf(TournamentState())
    val tournaments: State<TournamentState> = _tournaments

    private val _allStages = mutableStateOf(StageState())
    val allStages: State<StageState> = _allStages

    private val _stages = mutableListOf<TournamentStage?>()
    val stages: MutableList<TournamentStage?> = _stages

    private val _chosenTournament = mutableStateOf(Tournament())
    val chosenTournament: State<Tournament> = _chosenTournament

    private var mapCenterIsSet = false

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
        getAllStages()
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

    fun centerMap() {
        _centerLocation.value = _userLocation.value
    }

    fun showStages(tournament: Tournament) {
        _stages.clear()
        _chosenTournament.value = tournament
        _stages.addAll(ArrayList(allStages.value.data!!.filter { it!!.tournamentId == tournament.id }))
    }

    fun hideStages() {
        _stages.clear()
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

    private fun getAllStages() = viewModelScope.launch {
        tRepo.getStages().collect { result ->
            when (result) {
                is Response.Success -> {
                    _allStages.value =
                        StageState(data = result.data, isLoading = false, errorMsg = null)
                }

                is Response.Failure -> {
                    _allStages.value =
                        StageState(data = null, isLoading = false, errorMsg = result.e.message)
                }

                is Response.Loading -> {
                    _allStages.value =
                        StageState(data = null, isLoading = true, errorMsg = null)
                }
            }
        }
    }
}
