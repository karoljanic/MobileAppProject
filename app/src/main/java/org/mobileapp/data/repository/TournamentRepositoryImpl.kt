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
import org.mobileapp.data.repository.Values.BEST_SCORE
import org.mobileapp.data.repository.Values.PLAYERS
import org.mobileapp.data.repository.Values.PLAYER_UID
import org.mobileapp.data.repository.Values.STAGES
import org.mobileapp.data.repository.Values.TOURNAMENTS
import org.mobileapp.data.repository.Values.USERS
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.model.TournamentStage
import org.mobileapp.domain.repository.TournamentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TournamentRepositoryImpl @Inject constructor(
    private val db: FirebaseDatabase
) : TournamentRepository {

    override suspend fun createTournament(tournament: Tournament): Flow<Response<String>> =
        callbackFlow {
            val tournamentId = db.reference.push().key!!
            val newTournament = Tournament(
                id = tournamentId,
                name = tournament.name,
                ownerName = tournament.ownerName,
                ownerUID = tournament.ownerUID
            )

            db.reference.child(TOURNAMENTS).child(tournamentId).setValue(newTournament)
                .addOnSuccessListener { trySend(Response.Success("Tournament Added")) }
                .addOnFailureListener { trySend(Response.Failure(Throwable(it.message))) }
            awaitClose { close() }
        }

    override suspend fun createStage(stage: TournamentStage): Flow<Response<String>> =
        callbackFlow {
            val stageId = db.reference.push().key!!
            val newStage = TournamentStage(
                id = stageId,
                tournamentId = stage.tournamentId,
                gameType = stage.gameType,
                latitude = stage.latitude,
                longitude = stage.longitude,
                players = stage.players
            )

            db.reference.child(STAGES).child(stageId).setValue(newStage)
                .addOnSuccessListener { trySend(Response.Success("Tournament Added")) }
                .addOnFailureListener { trySend(Response.Failure(Throwable(it.message))) }

            awaitClose { close() }
        }

    override suspend fun getTournaments(): Flow<Response<List<Tournament?>>> = callbackFlow {
        trySend(Response.Loading)
        db.reference.keepSynced(true)
        val event = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tournaments = snapshot.child(TOURNAMENTS).children.map { dataSnapshot ->
                    dataSnapshot.getValue<Tournament>()
                }
                trySend(Response.Success(tournaments))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Failure(Throwable(error.message)))
            }
        }
        db.reference.addValueEventListener(event)
        awaitClose { close() }
    }

    override suspend fun getStages(): Flow<Response<List<TournamentStage?>>> = callbackFlow {
        trySend(Response.Loading)
        db.reference.keepSynced(true)
        val event = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stages = snapshot.child(STAGES).children.map { dataSnapshot ->
                    dataSnapshot.getValue<TournamentStage>()
                }
                trySend(Response.Success(stages))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Failure(Throwable(error.message)))
            }
        }
        db.reference.addValueEventListener(event)
        awaitClose { close() }
    }

    override suspend fun updateTournament(tournament: Tournament): Flow<Response<String>> =
        callbackFlow {
            db.reference.child(TOURNAMENTS).child(tournament.id!!)
                .updateChildren(tournament.toMap())
                .addOnSuccessListener { trySend(Response.Success("Tournament updated")) }
                .addOnFailureListener { trySend(Response.Failure(Throwable(it.message))) }
            awaitClose { close() }
        }

    override suspend fun updateStage(stage: TournamentStage): Flow<Response<String>> =
        callbackFlow {
            db.reference.child(STAGES).child(stage.id!!).updateChildren(stage.toMap())
                .addOnSuccessListener { trySend(Response.Success("Stage updated")) }
                .addOnFailureListener { trySend(Response.Failure(Throwable(it.message))) }
            awaitClose { close() }
        }

    override suspend fun deleteTournament(tournament: Tournament): Flow<Response<String>> =
        callbackFlow {
            db.reference.child(TOURNAMENTS).child(tournament.id!!).removeValue()
                .addOnSuccessListener { trySend(Response.Success("Tournament deleted")) }
                .addOnFailureListener { trySend(Response.Failure(Throwable(it.message))) }
            awaitClose { close() }
        }

    override suspend fun updateScore(
        stageId: String, userId: String, newScore: Int
    ): Flow<Response<String>> = callbackFlow {
        val ref = db.reference.child(STAGES).child(stageId).child(PLAYERS).orderByChild(PLAYER_UID)
            .equalTo(userId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userKey = snapshot.children.first().key!!

                    db.getReference(STAGES).child(stageId).child(PLAYERS).child(userKey).child(BEST_SCORE).setValue(newScore)
                        .addOnSuccessListener { trySend(Response.Success("Score updated")) }
                        .addOnFailureListener { trySend(Response.Failure(Throwable(it.message))) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Response.Failure(Throwable(error.message)))
            }
        })

        awaitClose { close() }
    }
}