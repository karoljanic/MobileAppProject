package org.mobileapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.model.TournamentStage

interface TournamentRepository {
    suspend fun createTournament(tournament: Tournament): Flow<Response<String>>
    suspend fun createStage(stage: TournamentStage) : Flow<Response<String>>
    suspend fun getTournaments(): Flow<Response<List<Tournament?>>>
    suspend fun getStages(): Flow<Response<List<TournamentStage?>>>
    suspend fun updateTournament(tournament: Tournament): Flow<Response<String>>
    suspend fun updateStage(stage: TournamentStage): Flow<Response<String>>
    suspend fun deleteTournament(tournament: Tournament): Flow<Response<String>>
    //suspend fun updateScore(stageId: String, userId: String, newScore: Int): Flow<Response<String>>
}