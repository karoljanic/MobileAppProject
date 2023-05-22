package org.mobileapp.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mobileapp.domain.repository.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repo: ProfileRepository
): ViewModel() {
    val photoUrl get() = repo.photoUrl
}