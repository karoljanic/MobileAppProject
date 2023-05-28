package org.mobileapp.ui.login

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import org.mobileapp.ui.login.components.LogInWithGoogle
import org.mobileapp.ui.login.components.LoginButton
import org.mobileapp.ui.login.components.OneTapLogIn
import org.mobileapp.viewmodel.LoginViewModel


@Composable
fun LoginView(viewModel: LoginViewModel = hiltViewModel(), navigateToMapScreen: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter
    ) {
        LoginButton(onClick = {
            viewModel.oneTapSignIn()
        })
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credentials =
                        viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credentials.googleIdToken
                    val googleCredentials = getCredential(googleIdToken, null)
                    viewModel.signInWithGoogle(googleCredentials)
                } catch (it: ApiException) {
                    Log.i("API ERROR", it.toString())
                }
            }
        }

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }


    OneTapLogIn(launch = { launch(it) })

    LogInWithGoogle(navigateToHomeScreen = { signedIn ->
        if (signedIn) {
            navigateToMapScreen()
        }
    })
}