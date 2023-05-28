package org.mobileapp.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mobileapp.domain.model.LeaderboardState
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.repository.LeaderboardRepository
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.domain.repository.RevokeAccessResponse
import org.mobileapp.domain.repository.SignOutResponse
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val pRepo: ProfileRepository, private val lRepo: LeaderboardRepository

) : ViewModel() {
    val displayName get() = pRepo.displayName
    val photoUrl get() = pRepo.photoUrl
    val userUID get() = pRepo.uid

    private val _totalScore = MutableStateFlow(LeaderboardState())
    val totalScore: StateFlow<LeaderboardState> = _totalScore

    init {
        getTotalScore(userUID)
    }

    var signOutResponse by mutableStateOf<SignOutResponse>(Response.Success(false))
        private set
    var revokeAccessResponse by mutableStateOf<RevokeAccessResponse>(Response.Success(false))
        private set

    fun signOut() = viewModelScope.launch {
        signOutResponse = Response.Loading
        signOutResponse = pRepo.signOut()
    }

    fun revokeAccess() = viewModelScope.launch {
        revokeAccessResponse = Response.Loading
        revokeAccessResponse = pRepo.revokeAccess()
    }

    private fun getTotalScore(playerId: String) = viewModelScope.launch {
        lRepo.getTotalScore(playerId).collect { result ->
            when (result) {
                is Response.Success -> {
                    _totalScore.update {
                        it.copy(
                            data = listOf(result.data), isLoading = false, errorMsg = null
                        )
                    }
                }

                is Response.Failure -> {
                    _totalScore.update {
                        it.copy(data = null, isLoading = false, errorMsg = result.e.message)
                    }
                }

                is Response.Loading -> {
                    _totalScore.update {
                        it.copy(
                            data = null, isLoading = true, errorMsg = null
                        )
                    }
                }
            }
        }
    }
}