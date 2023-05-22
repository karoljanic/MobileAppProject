package org.mobileapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.mobileapp.ui.StringValues
import org.mobileapp.ui.profile.components.RevokeAccess
import org.mobileapp.ui.profile.components.SignOut
import org.mobileapp.viewmodel.SettingsViewModel

@Composable
fun SettingsView(viewModel: SettingsViewModel = hiltViewModel(), navigateToAuthScreen: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    SignOut(
        navigateToAuthScreen = { signedOut ->
            if (signedOut) {
                navigateToAuthScreen()
            }
        }
    )

    fun showSnackBar() = coroutineScope.launch {
        val result = scaffoldState.snackbarHostState.showSnackbar(
            message = StringValues.REVOKE_ACCESS_MESSAGE,
            actionLabel = StringValues.LOG_OUT
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.signOut()
        }
    }

    RevokeAccess(
        navigateToAuthScreen = { accessRevoked ->
            if (accessRevoked) {
                navigateToAuthScreen()
            }
        },
        showSnackBar = {
            showSnackBar()
        }
    )

    Scaffold(scaffoldState = scaffoldState) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Button(onClick = {viewModel.signOut()}) {
                Text(text = "Sign out")
            }
            Button(onClick = {viewModel.revokeAccess()}) {
                Text(text = "Revoke access")
            }
        }
    }
}