package org.mobileapp.ui.profilemenu.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import org.mobileapp.viewmodel.ProfileViewModel

@Composable
fun ProfileView(
    viewModel: ProfileViewModel = hiltViewModel(), navigateToAuthScreen: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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

        Text(text = "Your Total Score: ${1087}")

        Spacer(modifier = Modifier.height(48.dp))

        Button(modifier = Modifier.fillMaxWidth(0.5F), onClick = { viewModel.signOut() }) {
            Text(text = "Sign out")
        }

        Button(modifier = Modifier.fillMaxWidth(0.5F), onClick = { viewModel.revokeAccess() }) {
            Text(text = "Revoke access")
        }

        LogOut(navigateToAuthScreen = { signedOut ->
            if (signedOut) {
                navigateToAuthScreen()
            }
        })

        RevokeAccess(navigateToAuthScreen = { accessRevoked ->
            if (accessRevoked) {
                navigateToAuthScreen()
            }
        }

        )
    }
}
