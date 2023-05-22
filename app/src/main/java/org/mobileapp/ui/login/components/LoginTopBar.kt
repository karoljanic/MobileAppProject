package org.mobileapp.ui.login.components

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.mobileapp.R
import org.mobileapp.ui.StringValues

@Composable
fun LoginTopBar() {
    TopAppBar (
        title = {
            Text(
                text = StringValues.LOGIN_SCREEN
            )
        }
    )
}