package org.mobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mobileapp.domain.model.LeaderboardState
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.TournamentState
import org.mobileapp.domain.repository.LeaderboardRepository
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(private val repo: LeaderboardRepository) :
    ViewModel() {

    private val _leaderboardState = MutableStateFlow(LeaderboardState())
    val leaderboardState: StateFlow<LeaderboardState> = _leaderboardState.asStateFlow()

    init {
        getTopN(10)
    }

    private fun getTopN(n: Int) = viewModelScope.launch {
        repo.getTopN(n).collect { result ->
            when (result) {
                is Response.Success -> {
                    _leaderboardState.update {
                        it.copy(
                            data = result.data, isLoading = false, errorMsg = null
                        )
                    }
                }

                is Response.Failure -> {
                    _leaderboardState.update {
                        it.copy(data = null, isLoading = false, errorMsg = result.e.message)
                    }
                }

                is Response.Loading -> {
                    _leaderboardState.update {
                        it.copy(
                            data = null, isLoading = true, errorMsg = null
                        )
                    }
                }
            }
        }
    }

}