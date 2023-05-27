package org.mobileapp.ui.profilemenu.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.domain.model.Response
import org.mobileapp.ui.global.ProgressBar
import org.mobileapp.viewmodel.ProfileViewModel

@Composable
fun RevokeAccess(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateToAuthScreen: (accessRevoked: Boolean) -> Unit,
    showSnackBar: () -> Unit
) {
    when(val revokeAccessResponse = viewModel.revokeAccessResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> revokeAccessResponse.data?.let { accessRevoked ->
            LaunchedEffect(accessRevoked) {
                navigateToAuthScreen(accessRevoked)
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(revokeAccessResponse.e)
            showSnackBar()
        }
    }
}