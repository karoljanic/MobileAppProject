package org.mobileapp.ui.map


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.ui.map.components.CenterMapButton
import org.mobileapp.ui.map.components.GoBackButton
import org.mobileapp.ui.map.components.GoToProfileButton
import org.mobileapp.ui.map.components.OSMap
import org.mobileapp.viewmodel.MapViewModel


@Composable
fun MapView(
    viewModel: MapViewModel = hiltViewModel(),
    navigateToProfileScreen: () -> Unit,
    navigateToGameScreen: (String, String, String, Double, Double) -> Unit
) {
    val context = LocalContext.current
    val stages = viewModel.stages

    Box {
        OSMap(viewContext = context, navigateToGameScreen = navigateToGameScreen)

        GoToProfileButton(
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.BottomStart),
            photoUrl = viewModel.photoUrl
        ) { navigateToProfileScreen.invoke() }

        CenterMapButton(
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.TopEnd)
        ) { viewModel.centerMap() }

        if (stages.isNotEmpty()) {
            GoBackButton(
                modifier = Modifier
                    .padding(15.dp)
                    .align(Alignment.BottomEnd)
            ) { viewModel.stages.clear() }
        }
    }
}

