package org.mobileapp.ui.login.components

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun LoginTopBar() {
    TopAppBar (
        title = {
            Text(
                text = "Authentication"
            )
        }
    )
}