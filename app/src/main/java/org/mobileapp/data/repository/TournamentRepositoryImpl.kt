package org.mobileapp.data.repository


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.mobileapp.data.repository.Values.TOURNAMENTS
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.repository.TournamentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TournamentRepositoryImpl @Inject constructor(
    private val db: FirebaseDatabase
) : TournamentRepository {

    override suspend fun createTournament(tournament: Tournament): Flow<Response<String>> =
        callbackFlow {
            val userId = db.reference.push().key!!
            val newTournament = Tournament(
                id = userId,
                name = tournament.name,
                owner = tournament.owner,
                stages = tournament.stages,
                players = tournament.players
            )
            db.reference.child(TOURNAMENTS).child(userId).setValue(newTournament)
                .addOnSuccessListener { trySend(Response.Success("User Added")) }
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

    override suspend fun updateTournament(tournament: Tournament): Flow<Response<String>> = callbackFlow {
        db.reference.child(tournament.id!!).updateChildren(tournament.toMap())
            .addOnSuccessListener { trySend(Response.Success("User updated")) }
            .addOnFailureListener { trySend(Response.Failure(Throwable(it.message))) }
        awaitClose { close() }
    }

    override suspend fun deleteTournament(tournament: Tournament): Flow<Response<String>> = callbackFlow {
        db.reference.child(tournament.id!!).removeValue()
            .addOnSuccessListener { trySend(Response.Success("User deleted")) }
            .addOnFailureListener { trySend(Response.Failure(Throwable(it.message))) }
        awaitClose { close() }
    }
}