package org.mobileapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.domain.repository.RevokeAccessResponse
import org.mobileapp.domain.repository.SignOutResponse
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: ProfileRepository
): ViewModel() {
    var signOutResponse by mutableStateOf<SignOutResponse>(Response.Success(false))
        private set
    var revokeAccessResponse by mutableStateOf<RevokeAccessResponse>(Response.Success(false))
        private set

    fun signOut() = viewModelScope.launch {
        signOutResponse = Response.Loading
        signOutResponse = repo.signOut()
    }

    fun revokeAccess() = viewModelScope.launch {
        revokeAccessResponse = Response.Loading
        revokeAccessResponse = repo.revokeAccess()
    }
}