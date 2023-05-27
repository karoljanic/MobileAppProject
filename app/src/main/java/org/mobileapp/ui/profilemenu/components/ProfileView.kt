package org.mobileapp.ui.profilemenu.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import org.mobileapp.ui.StringValues
import org.mobileapp.viewmodel.ProfileViewModel

@Composable
fun ProfileView(
    viewModel: ProfileViewModel = hiltViewModel(), navigateToAuthScreen: () -> Unit
    ) {

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    fun showSnackBar() = coroutineScope.launch {
        val result = scaffoldState.snackbarHostState.showSnackbar(
            message = StringValues.REVOKE_ACCESS_MESSAGE,
            actionLabel = StringValues.LOG_OUT
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.signOut()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(48.dp)
        )
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(viewModel.photoUrl)
                .crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .width(96.dp)
                .height(96.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = viewModel.displayName, fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = { viewModel.signOut()} ) {
            Text(text = "Sign out")
        }

        Button(onClick = { viewModel.revokeAccess()} ) {
            Text(text = "Revoke access")
        }

        SignOut(navigateToAuthScreen = { signedOut ->
            if (signedOut) {
                navigateToAuthScreen()
            }
        })

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

    }
}
