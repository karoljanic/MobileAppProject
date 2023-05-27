package org.mobileapp.ui.permission.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LocalizationNotAvailableDialog() {
    AlertDialog(
        onDismissRequest = { },
        buttons = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider()
                Text(
                    text = "OK",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = "Permission required")
        },
        text = {
            Text("Access to a location is required for the application to work properly.")
        },
        modifier = Modifier
    )
}