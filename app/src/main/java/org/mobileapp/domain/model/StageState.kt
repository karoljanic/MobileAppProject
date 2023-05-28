package org.mobileapp.domain.model

data class StageState(
    val data: List<TournamentStage?>? = null,
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)