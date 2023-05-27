package org.mobileapp.domain.model

data class TournamentState(
    val data: List<Tournament?>? = null,
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)