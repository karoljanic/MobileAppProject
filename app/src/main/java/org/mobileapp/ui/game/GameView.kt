package org.mobileapp.ui.game

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.toQuaternion
import org.mobileapp.R
import org.mobileapp.viewmodel.GameViewModel
import kotlin.math.abs

@Composable
fun GameView(
    stageId: String,
    playerId: String,
    gameType: String,
    latitude: Double,
    longitude: Double,
    backToMap: () -> Unit,
    gameViewModel: GameViewModel = hiltViewModel()
) {
    val draggableState = rememberDraggableState(onDelta = {})
    val info = remember { mutableStateOf("Placing Game") }

    gameViewModel.setBackToMapsCallback(backToMap)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .draggable(draggableState,
                Orientation.Vertical,
                reverseDirection = true,
                onDragStopped = { gameViewModel.game?.onSwipe(0f, it) })
    ) {
        ARScene(modifier = Modifier.fillMaxSize(),
            nodes = gameViewModel.nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.geospatialEnabled = true
                arSceneView.planeRenderer.isEnabled = false
            },
            onSessionCreate = { session ->
                session.instantPlacementEnabled = false

                gameViewModel.newGame(this, gameType, playerId, stageId)
            },
            onFrame = { arFrame ->
                gameViewModel.updateGame(arFrame)
                if (!gameViewModel.isPlaced) {
                    val earth = this.arSession?.earth ?: return@ARScene

                    if (earth.trackingState == TrackingState.TRACKING) {
                        val latDiff = abs(earth.cameraGeospatialPose.latitude - latitude)
                        val longDiff = abs(earth.cameraGeospatialPose.longitude - longitude)

                        if (longDiff in 0.00003..0.00015 && latDiff in 0.00002..0.00009) {
                            // Place the earth anchor at the same altitude as that of the camera to make it easier to view.
                            val altitude = earth.cameraGeospatialPose.altitude - 1f
                            val rotation = Rotation(0f, 0f, 0f)

                            val earthAnchor = earth.createAnchor(
                                latitude,
                                longitude,
                                altitude,
                                rotation.toQuaternion().toFloatArray()
                            )

                            Log.i(
                                "Game",
                                "Player ${earth.cameraGeospatialPose.latitude}, ${earth.cameraGeospatialPose.longitude}, ${earth.cameraGeospatialPose.altitude}"
                            )
                            Log.i("Game", "Game $latitude, $longitude, $altitude")

                            gameViewModel.anchorGame(earthAnchor)
                        } else {
                            info.value = "Somewhere Near You Balloons Will Appear!"
                        }
                    }
                }
            },
            onTap = { hitResult ->
                gameViewModel.onHitGame(hitResult)
            })

        if (!gameViewModel.isPlaced) {
            Snackbar(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter),
                backgroundColor = colorResource(R.color.purple_700),
                action = { }
            ) {
                Text(text = info.value, color = Color.White)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(colorResource(R.color.purple_700)),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Score: ${gameViewModel.score}",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.White
            )
            Text(
                text = "Time: ${"%.1f".format(gameViewModel.time)}",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.White
            )
        }

    }
}
