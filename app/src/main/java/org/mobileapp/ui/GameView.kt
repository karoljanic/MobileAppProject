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
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import org.mobileapp.game.BalloonGame
import org.mobileapp.game.Game

@Composable
fun GameView() {
    val nodes = remember { mutableStateListOf<ArNode>() }
    val game = remember { mutableStateOf<Game?>(null)}
    val isPlaced = remember { mutableStateOf(false) }
    val anchorVis = remember { mutableStateOf<ArModelNode?>(null)}

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.geospatialEnabled = true
            },
            onSessionCreate = { session ->
                session.instantPlacementEnabled = false

                anchorVis.value = ArModelNode(followHitPosition = true, placementMode = PlacementMode.BEST_AVAILABLE).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "models/Parrot.glb",
//                      glbFileLocation = "https://sceneview.github.io/assets/models/Spoons.glb",
                        onError = { Log.i("Loading", "$it") },
                        onLoaded = { Log.i("Loading", "$it") },
                        scaleToUnits = 0.9f,
                        centerOrigin = Position(y = -1.0f)
                    )

                    this@ARScene.addChild(this)
                    nodes.add(this)
                }

                game.value = BalloonGame(this@ARScene,nodes)
            },
            onFrame = { arFrame ->
                if (isPlaced.value) {
                    game.value?.onUpdate(arFrame)
                }
            },
            onTap = { hitResult ->
                if (game.value?.isRunning == true) {
                    Log.i("Game", "tapped")
                    game.value?.onHit(hitResult)
                }
            }
        )

        if (!isPlaced.value) {
            Button(
                onClick = {
                    anchorVis.value?.apply {
                        this.anchor()?.apply {
                            game.value?.anchor(this)
                            isPlaced.value = true
                            game.value?.start()
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