package org.mobileapp.domain.model

data class LeaderboardState (
    val data: List<LeaderboardPlayer?>? = null,
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)