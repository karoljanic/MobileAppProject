package org.mobileapp.data.repository

import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import org.mobileapp.data.repository.Values.USERS
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.domain.repository.RevokeAccessResponse
import org.mobileapp.domain.repository.SignOutResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    private var signInClient: GoogleSignInClient,
    private val db: FirebaseDatabase,
) : ProfileRepository {
    override val displayName = auth.currentUser?.displayName ?: "Placeholder name"
    override val photoUrl = auth.currentUser?.photoUrl?.toString() ?: "https://upload.wikimedia.org/wikipedia/commons/8/89/Portrait_Placeholder.png"
    override val uid = auth.uid ?: "Default UID"
    override suspend fun signOut(): SignOutResponse {
        return try {
            oneTapClient.signOut().await()
            auth.signOut()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun revokeAccess(): RevokeAccessResponse{
        return try {
            auth.currentUser?.apply {
                db.reference.child(USERS).child(uid).removeValue()
                signInClient.revokeAccess().await()
                oneTapClient.signOut().await()
                delete().await()
            }
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }
}