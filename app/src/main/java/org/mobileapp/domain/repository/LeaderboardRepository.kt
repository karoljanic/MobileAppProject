package org.mobileapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.mobileapp.domain.model.LeaderboardPlayer
import org.mobileapp.domain.model.Response

interface LeaderboardRepository {
    suspend fun getTopN(n : Int) : Flow<Response<List<LeaderboardPlayer?>>>
    suspend fun getTotalScore(playerId: String): Flow<Response<LeaderboardPlayer?>>
}