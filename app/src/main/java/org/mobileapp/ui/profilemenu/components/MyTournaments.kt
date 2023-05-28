package org.mobileapp.ui.profilemenu.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.domain.model.Tournament
import org.mobileapp.viewmodel.TournamentsViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun MyTournaments(
    viewModel: TournamentsViewModel = hiltViewModel()
) {
    var creatingMode by remember { mutableStateOf(false) }
    var newTournamentName by remember { mutableStateOf("") }

    val tournamentState by viewModel.tournamentState.collectAsState()
    val stageState by viewModel.stageState.collectAsState()

    if (creatingMode) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(20.dp),
                overflow = TextOverflow.Ellipsis,

                text = "You create a tournament with the given name. To add stage to the tournament, " + "go to the position where the stage is to be placed and update the tournament"
            )

            TextField(value = newTournamentName,
                onValueChange = { newTournamentName = it },
                label = { Text("Enter Tournament Name") })

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Button(onClick = {
                    creatingMode = false
                }) { Text("Cancel") }

                Button(onClick = {
                    if (newTournamentName != "") {
                        viewModel.createTournament(
                            Tournament(
                                name = newTournamentName,
                                ownerName = viewModel.userName,
                                ownerUID = viewModel.userID
                            )
                        )
                        creatingMode = false
                    }
                }) { Text("Create") }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(modifier = Modifier.padding(20.dp), onClick = {
                creatingMode = true
            }) { Text("Create New Tournament") }

            when {
                tournamentState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                !tournamentState.errorMsg.isNullOrEmpty() -> {
                    Text(text = tournamentState.errorMsg!!)
                }

                tournamentState.data.isNullOrEmpty() -> {
                    Text(text = "You Don't Have Any Tournaments.")
                }

                !tournamentState.data.isNullOrEmpty() -> {
                    LazyColumn {
                        itemsIndexed(tournamentState.data!!.filter { it!!.ownerUID == viewModel.userID }) { _, item ->
                            TournamentItem(
                                viewModel,
                                item!!,
                                ArrayList(stageState.data!!.filter { it!!.tournamentId == item.id }),
                                GeoPoint(viewModel.userLocation.value!!),
                                { t -> viewModel.deleteTournament(t) },
                                { t -> viewModel.updateTournament(t) },
                            )
                        }
                    }
                }
            }
        }
    }

}
