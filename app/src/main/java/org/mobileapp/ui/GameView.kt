package org.mobileapp.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import org.mobileapp.game.BalloonGame
import org.mobileapp.game.Game
import org.mobileapp.viewmodel.GameViewModel

@Composable
fun GameView(state: GameViewModel = viewModel()) {

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = state.nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.geospatialEnabled = true
            },
            onSessionCreate = { session ->
                session.instantPlacementEnabled = false

                state.anchorVis.value = ArModelNode(followHitPosition = true, placementMode = PlacementMode.BEST_AVAILABLE).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "models/Parrot.glb",
//                      glbFileLocation = "https://sceneview.github.io/assets/models/Spoons.glb",
                        onError = { Log.i("Loading", "$it") },
                        onLoaded = { Log.i("Loading", "$it") },
                        scaleToUnits = 0.9f,
                        centerOrigin = Position(y = -1.0f)
                    )

                    this@ARScene.addChild(this)
                    state.nodes.add(this)
                }

                state.game.value = BalloonGame(this@ARScene, state.nodes)
            },
            onFrame = { arFrame ->
                if (state.isPlaced.value) {
                    state.game.value?.onUpdate(arFrame)
                }
            },
            onTap = { hitResult ->
                if (state.game.value?.isRunning == true) {
                    Log.i("Game", "tapped")
                    state.game.value?.onHit(hitResult)
                }
            }
        )

        if (!state.isPlaced.value) {
            Button(
                onClick = {
                    state.anchorVis.value?.apply {
                        this.anchor()?.apply {
                            state.game.value?.anchor(this)
                            state.isPlaced.value = true
                            state.game.value?.start()
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)) {
                Text(text = "Place game")
            }
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