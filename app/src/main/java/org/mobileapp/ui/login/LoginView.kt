package org.mobileapp.ui.login

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import org.mobileapp.ui.login.components.LogInWithGoogle
import org.mobileapp.ui.login.components.LoginContent
import org.mobileapp.ui.login.components.LoginTopBar
import org.mobileapp.ui.login.components.OneTapLogIn
import org.mobileapp.viewmodel.LoginViewModel


@Composable
fun LoginView(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToMapScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            LoginTopBar()
        },
        content = { padding ->
            LoginContent(
                padding = padding,
                oneTapSignIn = {
                    viewModel.oneTapSignIn()
                }
            )
        }
    )

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val credentials = viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
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

    OneTapLogIn(
        launch = {
            launch(it)
        }
    )

    LogInWithGoogle(
        navigateToHomeScreen = { signedIn ->
            if (signedIn) {
                navigateToMapScreen()
            }
        }
    )
}