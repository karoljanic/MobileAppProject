package org.mobileapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.Tournament

interface TournamentRepository {
    suspend fun createTournament(tournament: Tournament): Flow<Response<String>>
    suspend fun getTournaments(): Flow<Response<List<Tournament?>>>
    suspend fun updateTournament(tournament: Tournament): Flow<Response<String>>
    suspend fun deleteTournament(tournament: Tournament): Flow<Response<String>>
}