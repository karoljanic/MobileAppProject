package org.mobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.StageState
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.model.TournamentStage
import org.mobileapp.domain.model.TournamentState
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.domain.repository.TournamentRepository
import org.mobileapp.service.TrackerService
import javax.inject.Inject

@HiltViewModel
class TournamentsViewModel @Inject constructor(
    private val tRepo: TournamentRepository, private val pRepo: ProfileRepository
): ViewModel() {

    val photoUrl get() = pRepo.photoUrl
    val userID get() = pRepo.uid
    val userName get() = pRepo.displayName

    val userLocation = TrackerService.currentLocation

    private val _tournamentState = MutableStateFlow(TournamentState())
    val tournamentState: StateFlow<TournamentState> = _tournamentState.asStateFlow()

    private val _stageState = MutableStateFlow(StageState())
    val stageState: StateFlow<StageState> = _stageState

    init {
        getTournaments()
        getStages()
    }

    private fun getTournaments() = viewModelScope.launch {
        tRepo.getTournaments().collect { result ->
            when (result) {
                is Response.Success -> {
                    _tournamentState.update {
                        it.copy(
                            data = result.data, isLoading = false, errorMsg = null
                        )
                    }
                }

                is Response.Failure -> {
                    _tournamentState.update {
                        it.copy(data = null, isLoading = false, errorMsg = result.e.message)
                    }
                }

                is Response.Loading -> {
                    _tournamentState.update {
                        it.copy(
                            data = null, isLoading = true, errorMsg = null
                        )
                    }
                }
            }
        }
    }

    private fun getStages() = viewModelScope.launch {
        tRepo.getStages().collect { result ->
            when (result) {
                is Response.Success -> {
                    _stageState.update {
                        it.copy(
                            data = result.data, isLoading = false, errorMsg = null
                        )
                    }
                }

                is Response.Failure -> {
                    _stageState.update {
                        it.copy(data = null, isLoading = false, errorMsg = result.e.message)
                    }
                }

                is Response.Loading -> {
                    _stageState.update {
                        it.copy(
                            data = null, isLoading = true, errorMsg = null
                        )
                    }
                }
            }
        }
    }

    fun createTournament(tournament: Tournament) = viewModelScope.launch {
        tRepo.createTournament(tournament).collect { result ->
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

    fun createStage(stage: TournamentStage) = viewModelScope.launch {
        tRepo.createStage(stage).collect { result ->
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
    fun updateTournament(tournament: Tournament) = viewModelScope.launch {
        tRepo.updateTournament(tournament).collect { result ->
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

    fun deleteTournament(tournament: Tournament) = viewModelScope.launch {
        tRepo.deleteTournament(tournament).collect { result ->
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