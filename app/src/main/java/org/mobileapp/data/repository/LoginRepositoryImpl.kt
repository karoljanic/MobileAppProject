package org.mobileapp.data.repository

import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import org.mobileapp.data.repository.Values.CREATED_AT
import org.mobileapp.data.repository.Values.DISPLAY_NAME
import org.mobileapp.data.repository.Values.EMAIL
import org.mobileapp.data.repository.Values.PHOTO_URL
import org.mobileapp.data.repository.Values.SIGN_IN_REQUEST
import org.mobileapp.data.repository.Values.SIGN_UP_REQUEST
import org.mobileapp.data.repository.Values.USERS
import org.mobileapp.domain.model.Response
import org.mobileapp.domain.repository.LoginRepository
import org.mobileapp.domain.repository.OneTapSignInResponse
import org.mobileapp.domain.repository.SignInWithGoogleResponse
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
    private val db: FirebaseDatabase
) : LoginRepository {
    override val isUserAuthenticatedInFirebase = auth.currentUser != null

    override suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse{
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            Response.Success(signInResult)
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                Response.Success(signUpResult)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }
    }

    override suspend fun signInWithGoogle(
        googleCredential: AuthCredential
    ): SignInWithGoogleResponse {
        return try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                addUser()
            }
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    private suspend fun addUser() {
        auth.currentUser?.apply {
            val user = toUser()
            db.reference.child(USERS).child(uid).setValue(user)
            db.getReference("test1").setValue("dziala")
            //db.c(USERS).document(uid).set(user).await()
            Log.i("DATABASE", "BYLEM TU")
        }
    }
}

fun FirebaseUser.toUser() = mapOf(
    DISPLAY_NAME to displayName,
    EMAIL to email,
    PHOTO_URL to photoUrl?.toString()
   // CREATED_AT to serverTimestamp()
)