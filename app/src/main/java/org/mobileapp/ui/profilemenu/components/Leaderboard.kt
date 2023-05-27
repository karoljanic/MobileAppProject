package org.mobileapp.ui.profilemenu.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.model.TournamentStage
import org.mobileapp.viewmodel.TournamentsViewModel

@Composable
fun Leaderboard(viewModel: TournamentsViewModel = hiltViewModel()) {
    /*
    LaunchedEffect(Unit) {
        viewModel.createTournament(
            Tournament(
                "id", "super name", "other owner", listOf(
                    TournamentStage(
                        1, "51.108233", "17.074922", 2, "{}"
                    ),
                    TournamentStage(
                        2, "51.106765", "17.075266", 3, "{}"
                    ),
                    TournamentStage(
                        3, "51.108031", "17.080866", 1, "{}"
                    ),
                    TournamentStage(
                        4, "51.108497", "17.082570", 3, "{}"
                    )
                ), listOf("player11", "player52", "player38", "player19", "player8")
            )
        )
    }
     */
}
