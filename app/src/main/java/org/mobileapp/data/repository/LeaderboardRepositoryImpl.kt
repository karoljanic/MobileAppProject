package org.mobileapp.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.mobileapp.domain.model.LeaderboardPlayer
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.repository.LeaderboardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val db : FirebaseDatabase
) : LeaderboardRepository {
    override suspend fun getTopN(n: Int): Flow<Response<List<LeaderboardPlayer?>>> = callbackFlow {
        trySend(Response.Loading)
        db.reference.keepSynced(true)
        val topPlayers = db.reference.child(Values.LEADERBOARD).orderByChild("totalScore").limitToLast(n)
        val event = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val players = snapshot.children.map { dataSnapshot ->
                    dataSnapshot.getValue<LeaderboardPlayer>()
                }
                trySend(Response.Success(players))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Failure(Throwable(error.message)))
            }
        }
        topPlayers.addValueEventListener(event)
        awaitClose { close() }
    }

    override suspend fun getTotalScore(playerId: String): Flow<Response<LeaderboardPlayer>> = callbackFlow {
        trySend(Response.Loading)
        db.reference.keepSynced(true)
        val playerScore = db.reference.child(Values.LEADERBOARD).child(playerId)
        val event = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val score = snapshot.getValue<LeaderboardPlayer>()
                trySend(Response.Success(score))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Failure(Throwable(error.message)))
            }
        }

        playerScore.addValueEventListener(event)
        awaitClose { close() }
    }
}