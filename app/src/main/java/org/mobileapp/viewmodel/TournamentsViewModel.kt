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
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.model.TournamentState
import org.mobileapp.domain.repository.TournamentRepository
import javax.inject.Inject

@HiltViewModel
class TournamentsViewModel @Inject constructor(
    private val repo: TournamentRepository
): ViewModel() {

    private val _tournamentState = MutableStateFlow(TournamentState())
    val tournamentState: StateFlow<TournamentState> = _tournamentState.asStateFlow()

    init {
        getTournaments()
    }

    private fun getTournaments() = viewModelScope.launch {
        repo.getTournaments().collect { result ->
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

    fun createTournament(tournament: Tournament) = viewModelScope.launch {
        repo.createTournament(tournament).collect { result ->
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
        repo.updateTournament(tournament).collect { result ->
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
        repo.deleteTournament(tournament).collect { result ->
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