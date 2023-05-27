package org.mobileapp.di

import android.app.Application
import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.mobileapp.R
import org.mobileapp.data.repository.Values.SIGN_IN_REQUEST
import org.mobileapp.data.repository.Values.SIGN_UP_REQUEST
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    fun provideFirebaseDatabase() = Firebase.database
}