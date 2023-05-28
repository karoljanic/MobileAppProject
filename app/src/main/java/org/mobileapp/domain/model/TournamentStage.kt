package org.mobileapp.domain.model

import com.google.firebase.database.Exclude

data class TournamentStage(
    val id: String? = null,
    val tournamentId: String? = null,
    val gameType: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    var players: ArrayList<TournamentPlayer>? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "tournamentId" to tournamentId,
            "gameType" to gameType,
            "latitude" to latitude,
            "longitude" to longitude,
            "players" to players,
        )
    }
}
