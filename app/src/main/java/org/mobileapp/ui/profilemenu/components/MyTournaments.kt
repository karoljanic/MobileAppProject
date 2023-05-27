package org.mobileapp.ui.profilemenu.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.viewmodel.ProfileViewModel
import org.mobileapp.viewmodel.TournamentsViewModel

@Composable
fun MyTournaments(
    tViewModel: TournamentsViewModel = hiltViewModel(),
    pViewModel: ProfileViewModel = hiltViewModel()
) {
    val creatingMode = remember { mutableStateOf(false) }

    val tournamentState by tViewModel.tournamentState.collectAsState()

    if (creatingMode.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(modifier = Modifier.height(height = 40.dp),
                    value = "tournament name", onValueChange = {})

            Row(modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly) {

                Button(onClick = {
                    creatingMode.value = false
                }) { Text("Cancel!") }

                Button(onClick = {
                    creatingMode.value = false
                }) { Text("Create!") }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 20.dp, 0.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {
                creatingMode.value = true
            }) { Text("Create New Tournament!") }

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
                        itemsIndexed(tournamentState.data!!) { index, item ->
                            TournamentItem(
                                name = item?.name!!,
                                players = item.players!!,
                                stages = item.stages!!
                                // onUpdateClick = { id, name ->
                                //    tViewModel.updateTournament(RealtimeDBUser(id, name))
                                // },
                                //onDeleteClick = { id, name ->
                                //   tViewModel.deleteTournament(RealtimeDBUser(id, name))
                                // })
                            )
                        }
                    }
                }
            }
        }
    }

}
