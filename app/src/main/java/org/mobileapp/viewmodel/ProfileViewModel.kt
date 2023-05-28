package org.mobileapp.viewmodel


import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.domain.repository.RevokeAccessResponse
import org.mobileapp.domain.repository.SignOutResponse
import org.mobileapp.domain.repository.TournamentRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val pRepo: ProfileRepository, private val tRepo: TournamentRepository

) : ViewModel() {
    val displayName get() = pRepo.displayName
    val photoUrl get() = pRepo.photoUrl
    val userUID get() = pRepo.uid

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
}