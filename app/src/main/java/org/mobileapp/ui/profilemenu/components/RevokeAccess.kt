package org.mobileapp.ui.profilemenu.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.domain.model.Response
import org.mobileapp.viewmodel.ProfileViewModel

@Composable
fun RevokeAccess(
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateToAuthScreen: (accessRevoked: Boolean) -> Unit
) {
    when(val revokeAccessResponse = viewModel.revokeAccessResponse) {
        is Response.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){ CircularProgressIndicator() }
        is Response.Success -> revokeAccessResponse.data?.let { accessRevoked ->
            LaunchedEffect(accessRevoked) {
                navigateToAuthScreen(accessRevoked)
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(revokeAccessResponse.e)
        }
    }
}