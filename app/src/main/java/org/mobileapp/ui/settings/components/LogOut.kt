package org.mobileapp.ui.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.domain.model.Response
import org.mobileapp.ui.global.ProgressBar
import org.mobileapp.viewmodel.ProfileViewModel
import org.mobileapp.viewmodel.SettingsViewModel

@Composable
fun SignOut(
    viewModel: SettingsViewModel = hiltViewModel(),
    navigateToAuthScreen: (signedOut: Boolean) -> Unit
) {
    when(val signOutResponse = viewModel.signOutResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> signOutResponse.data?.let { signedOut ->
            LaunchedEffect(signedOut) {
                navigateToAuthScreen(signedOut)
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(signOutResponse.e)
        }
    }
}