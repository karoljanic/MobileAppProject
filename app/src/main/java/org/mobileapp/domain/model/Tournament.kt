package org.mobileapp.domain.model

import com.google.firebase.database.Exclude

data class Tournament(
    val id: String? = null,
    val name: String? = null,
    val owner: String? = null,
    val stages: List<TournamentStage>? = null,
    val players: List<String>? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "owner" to owner,
            "stages" to stages,
            "players" to players
        )
    }
}
