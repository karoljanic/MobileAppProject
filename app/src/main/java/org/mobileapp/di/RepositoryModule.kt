package org.mobileapp.di

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.mobileapp.data.repository.LeaderboardRepositoryImpl
import org.mobileapp.data.repository.LoginRepositoryImpl
import org.mobileapp.data.repository.ProfileRepositoryImpl
import org.mobileapp.data.repository.TournamentRepositoryImpl
import org.mobileapp.data.repository.Values
import org.mobileapp.domain.repository.LeaderboardRepository
import org.mobileapp.domain.repository.LoginRepository
import org.mobileapp.domain.repository.ProfileRepository
import org.mobileapp.domain.repository.TournamentRepository
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {
    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        oneTapClient: SignInClient,
        @Named(Values.SIGN_IN_REQUEST) signInRequest: BeginSignInRequest,
        @Named(Values.SIGN_UP_REQUEST) signUpRequest: BeginSignInRequest,
        db: FirebaseDatabase
    ): LoginRepository = LoginRepositoryImpl(
        auth = auth,
        oneTapClient = oneTapClient,
        signInRequest = signInRequest,
        signUpRequest = signUpRequest,
        db = db
    )

    @Provides
    fun provideProfileRepository(
        auth: FirebaseAuth,
        oneTapClient: SignInClient,
        signInClient: GoogleSignInClient,
        db: FirebaseDatabase
    ): ProfileRepository = ProfileRepositoryImpl(
        auth = auth, oneTapClient = oneTapClient, signInClient = signInClient, db = db
    )

    @Provides
    fun provideTournamentRepository(
        db: FirebaseDatabase,
        auth: FirebaseAuth
    ): TournamentRepository = TournamentRepositoryImpl(
        db = db,
        auth = auth,

        )

    @Provides
    fun provideLeaderboardRepository(
        db: FirebaseDatabase
    ): LeaderboardRepository = LeaderboardRepositoryImpl(
        db = db
    )
}