package org.mobileapp.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.math.toQuaternion
import io.github.sceneview.node.ModelNode

@Composable
fun GameView() {
    val nodes = remember { mutableStateListOf<ArNode>() }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.geospatialEnabled = true
            },
            onSessionCreate = { session ->

            },
            onFrame = { arFrame ->

            },
            onTap = { hitResult ->

                Log.i("loading", "tapped")

                val node = ArModelNode(
                    placementMode = PlacementMode.BEST_AVAILABLE,
                    instantAnchor = true,
                ).apply {
                    applyPoseRotation = true
                    loadModelGlbAsync(
                        glbFileLocation = "models/Parrot.glb",
//                        glbFileLocation = "https://sceneview.github.io/assets/models/Spoons.glb",
                        onError = { Log.i("loading", "$it") },
                        onLoaded = { Log.i("loading", "$it") },
                        scaleToUnits = 0.9f,
                        centerOrigin = Position(y = -1.0f)
                    )
                }

                this.addChild(
                    node
                )

                nodes.add(node)

//                val node = ArModelNode().apply {
//                    applyPoseRotation = true
//                    loadModelGlbAsync(
//                        glbFileLocation = "models/Parrot.glb",
////                        glbFileLocation = "https://sceneview.github.io/assets/models/Spoons.glb",
//                        onError = { Log.i("loading", "$it") },
//                        onLoaded = { Log.i("loading", "$it") },
//                        scaleToUnits = 0.9f,
//                        centerOrigin = Position(y = -1.0f)
//                    )
//                }
//
//                val earth = this.arSession?.earth?: return@ARScene
//                Log.i("loading", "ok")
//
//                if (earth.trackingState == TrackingState.TRACKING) {
//                    // Place the earth anchor at the same altitude as that of the camera to make it easier to view.
//                    val altitude = earth.cameraGeospatialPose.altitude - 1f
//                    val rotation = Rotation(0f, 0f, 0f)
//                    // Put the anchor somewhere around the user.
//                    val latitude = earth.cameraGeospatialPose.latitude + 0.0004
//                    val longitude = earth.cameraGeospatialPose.longitude + 0.0004
//                    val earthAnchor = earth.createAnchor(latitude, longitude, altitude, rotation.toQuaternion().toFloatArray())
//
//                    Log.i("loading", "$latitude, $longitude, $altitude")
//
//                    node.anchor = earthAnchor
//
//                    addChild(node)
//
//                    nodes.add(node)
//                }
//                else {
//                    Log.i("loading", "$earth")
//                }
            }
        )
    }
}