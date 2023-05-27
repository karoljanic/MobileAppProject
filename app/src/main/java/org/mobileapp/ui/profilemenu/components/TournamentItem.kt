package org.mobileapp.ui.profilemenu.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mobileapp.domain.model.TournamentStage

@Composable
fun TournamentItem(
    name: String, stages: List<TournamentStage>, players: List<String>
) {
    val showUpdateDialog = remember { mutableStateOf(false) }

    Row(
        Modifier
            .clickable { showUpdateDialog.value = true }
            .fillMaxWidth()
            .padding(20.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                text = name, style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "stages: ${stages.size}")
                Text(text = "participants: ${players.size}")
                Text(text = "best score: ${976}")
            }

        }
        //IconButton(onClick = {
        //    onDeleteClick(userId, userName)
        //}) {
        //    Icon(Icons.Default.Delete, contentDescription = "Delete")
        //}
    }

}