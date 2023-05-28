package org.mobileapp.ui.profilemenu.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.viewmodel.LeaderboardViewModel

@Composable
fun Leaderboard(viewModel: LeaderboardViewModel = hiltViewModel()) {
    val leaderboardState by viewModel.leaderboardState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Top 10 Best Players",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        when {
            leaderboardState.isLoading -> {
                CircularProgressIndicator()
            }

            !leaderboardState.errorMsg.isNullOrEmpty() -> {
                Text(text = leaderboardState.errorMsg!!)
            }

            leaderboardState.data.isNullOrEmpty() -> {
                Text(text = "Empty Leaderboard")
            }

            !leaderboardState.data.isNullOrEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    itemsIndexed(leaderboardState.data!!.reversed()) { _, item ->
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item!!.playerName}",
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.weight(0.5f)
                            )
                            Text(
                                text = "Score : ${item.totalScore}",
                                modifier = Modifier.weight(0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
