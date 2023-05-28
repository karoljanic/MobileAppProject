package org.mobileapp.ui.profilemenu.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.model.TournamentPlayer
import org.mobileapp.domain.model.TournamentStage
import org.mobileapp.viewmodel.TournamentsViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun TournamentItem(
    viewModel: TournamentsViewModel = hiltViewModel(),
    tournament: Tournament,
    stages: ArrayList<TournamentStage?>,
    currentLocation: GeoPoint,
    delete: (Tournament) -> Unit,
    update: (Tournament) -> Unit
) {
    val gameTypes = listOf("Game 1", "Game 2", "Game 3")

    var editingMode by remember { mutableStateOf(false) }
    var newTournamentName by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf(gameTypes[0]) }
    var expanded by remember { mutableStateOf(false) }

    val players: ArrayList<ArrayList<TournamentPlayer>> = ArrayList()

    stages.forEach { players.addAll((listOf(it!!.players ?: ArrayList()))) }

    Row(
        Modifier
            .clickable { editingMode = true }
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = tournament.name!!, style = TextStyle(
                    fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "stages: ${stages.size}")
                Text(text = "participants: ${players.flatten().distinctBy { it.playerUID }.size}")
                Text(text = "best score: ${players.flatten().maxByOrNull { it.bestScore } ?: '-'}")
            }

            if (editingMode) {
                Spacer(modifier = Modifier.height(24.dp))

                TextField(value = newTournamentName,
                    onValueChange = { newTournamentName = it },
                    label = { Text("Enter New Name") })

                Button(onClick = {
                    if(newTournamentName.isNotEmpty()) {
                        update(Tournament(id = tournament.id, name = newTournamentName, ownerUID = tournament.ownerUID, ownerName = tournament.ownerName))
                    }
                }) {
                    Text(text = "Update Name")
                }

                Spacer(modifier = Modifier.height(12.dp))

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    gameTypes.forEach { option ->
                        DropdownMenuItem(onClick = {
                            selectedGame = option
                            expanded = false
                        }) {
                            Text(text = option)
                        }
                    }
                }
                TextButton(onClick = { expanded = true }) {
                    Text(text = selectedGame)
                }

                Button(onClick = {
                    viewModel.createStage(
                        TournamentStage(
                            tournamentId = tournament.id,
                            gameType = selectedGame,
                            latitude = currentLocation.latitude,
                            longitude =  currentLocation.longitude
                        )
                    )
                }) {
                    Text(text = "Add Stage At Your Current Location")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { delete(tournament)  }) {
                    Text(text = "Delete")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { editingMode = false }) {
                    Text(text = "Exit Editing Mode")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}