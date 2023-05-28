package org.mobileapp.ui

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.toQuaternion
import org.mobileapp.viewmodel.GameViewModel
import kotlin.math.abs

@Composable
fun GameView(
    stageId: String,
    playerId: String,
    gameType: String,
    latitude: Double,
    longitude: Double,
    gameViewModel: GameViewModel = viewModel()
) {
    val draggableState = rememberDraggableState(onDelta = {})
    val info = remember { mutableStateOf("Placing Game") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .draggable(
                draggableState,
                Orientation.Vertical,
                reverseDirection = true,
                onDragStopped = { gameViewModel.game?.onSwipe(0f, it) })
    ) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = gameViewModel.nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.geospatialEnabled = true
                arSceneView.planeRenderer.isEnabled = false
            },
            onSessionCreate = { session ->
                session.instantPlacementEnabled = false

                gameViewModel.newGame(this, gameType)
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

                            Log.i("Game", "Player ${earth.cameraGeospatialPose.latitude}, ${earth.cameraGeospatialPose.longitude}, ${earth.cameraGeospatialPose.altitude}")
                            Log.i("Game", "Game $latitude, $longitude, $altitude")

                            gameViewModel.anchorGame(earthAnchor)
                        } else {
                            info.value = "$latDiff $longDiff"
                        }
                    }
                }
            },
            onTap = { hitResult ->
                gameViewModel.onHitGame(hitResult)
            }
        )

        if (!gameViewModel.isPlaced) {
            Text(info.value, modifier = Modifier.align(Alignment.BottomCenter))
        }

        Column(modifier = Modifier.align(Alignment.TopCenter)) {
            Text(text = "Score ${gameViewModel.score}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(text = "Time ${gameViewModel.time}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        }

    }
}

//Przykład użycia geospatial
//val earth = this.arSession?.earth?: return@ARScene
//Log.i("loading", "ok")
//
//if (earth.trackingState == TrackingState.TRACKING) {
//    // Place the earth anchor at the same altitude as that of the camera to make it easier to view.
//    val altitude = earth.cameraGeospatialPose.altitude - 1f
//    val rotation = Rotation(0f, 0f, 0f)
//    // Put the anchor somewhere around the user.
//    val latitude = earth.cameraGeospatialPose.latitude + 0.0004
//    val longitude = earth.cameraGeospatialPose.longitude + 0.0004
//    val earthAnchor = earth.createAnchor(latitude, longitude, altitude, rotation.toQuaternion().toFloatArray())
//
//    Log.i("loading", "$latitude, $longitude, $altitude")
//
//    node.anchor = earthAnchor
//
//    addChild(node)
//
//    nodes.add(node)
//}
//else {
//    Log.i("loading", "$earth")
//}

//resolve anchor in terrian
// state.anchorGame