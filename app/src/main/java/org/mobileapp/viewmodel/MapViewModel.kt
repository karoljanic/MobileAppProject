package org.mobileapp.viewmodel


import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.mobileapp.data.configuration.MapConfig
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.StageState
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.model.TournamentPlayer
import org.mobileapp.domain.model.TournamentStage
import org.mobileapp.domain.model.TournamentState
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.domain.repository.TournamentRepository
import org.mobileapp.service.TrackerService
import org.mobileapp.utils.TournamentUtils
import org.osmdroid.util.GeoPoint
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val pRepo: ProfileRepository, private val tRepo: TournamentRepository
) : ViewModel() {
    val photoUrl get() = pRepo.photoUrl
    val userID get() = pRepo.uid

    val userName get() = pRepo.displayName

    private val _centerLocation = mutableStateOf<GeoPoint?>(null)
    val centerLocation: State<GeoPoint?> = _centerLocation

    private val _userLocation = mutableStateOf<GeoPoint?>(null)
    val userLocation: State<GeoPoint?> = _userLocation

    private val _mapZoom = mutableStateOf<Double>(MapConfig.DEFAULT_MAP_ZOOM)
    val mapZoom: State<Double> = _mapZoom

    private val _tournaments = mutableStateOf(TournamentState())
    val tournaments: State<TournamentState> = _tournaments

    private val _allStages = mutableStateOf(StageState())

    private val _stages = mutableListOf<TournamentStage?>()
    val stages: MutableList<TournamentStage?> = _stages

    private val _chosenTournament = mutableStateOf(Tournament())

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

    fun calculateTournamentCenter(tournament: Tournament): GeoPoint {
        return TournamentUtils.findCenter(_allStages.value.data!!.filter { it!!.tournamentId == tournament.id })
    }

    fun chosenTournament(tournament: Tournament) {
        _chosenTournament.value = tournament
        _stages.clear()
        _chosenTournament.value = tournament
        _stages.addAll(ArrayList(_allStages.value.data!!.filter { it!!.tournamentId == tournament.id }))
    }

    fun chosenStage(stage: TournamentStage) {
        _stages.clear()

        if (stage.players == null) {
            stage.players = arrayListOf(TournamentPlayer(userID, userName, 0))
        } else {
            if (!stage.players!!.any { user -> user.playerUID == userID }) {
                stage.players!!.removeAll { user -> user.playerUID == userID }
                stage.players!!.add(TournamentPlayer(userID, userName, 0))
            }
        }

        updateStage(stage)
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

    private fun updateStage(stage: TournamentStage) = viewModelScope.launch {
        tRepo.updateStage(stage).collect { result ->
            when (result) {
                is Response.Success -> {
                    //Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                }

                is Response.Failure -> {
                    //Toast.makeText(context, result.exception.message, Toast.LENGTH_SHORT).show()
                }

                is Response.Loading -> {
                }
            }
        }
    }
}
