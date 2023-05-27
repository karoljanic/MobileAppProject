package org.mobileapp.ui.profilemenu.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mobileapp.domain.model.Tournament


@Composable
fun TournamentView(tournament: Tournament) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = tournament.name!!)
        Text(text = "created by ${tournament.owner}")

    }
}