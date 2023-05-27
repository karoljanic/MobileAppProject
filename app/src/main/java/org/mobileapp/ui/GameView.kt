package org.mobileapp.ui

import android.graphics.Color
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import org.mobileapp.game.BalloonGame
import org.mobileapp.viewmodel.GameViewModel

@Composable
fun GameView(state: GameViewModel = viewModel()) {
    val draggableState = rememberDraggableState(onDelta = {})

    Box(
        modifier = Modifier
            .fillMaxSize()
            .draggable(
                draggableState,
                Orientation.Vertical,
                reverseDirection = true,
                onDragStopped = { state.game?.onSwipe(0f, it) })
    ) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = state.nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.geospatialEnabled = true
            },
            onSessionCreate = { session ->
                session.instantPlacementEnabled = false

                state.newAnchorVis(this)

                state.newGame(this)
            },
            onFrame = { arFrame ->
                state.updateGame(arFrame)
            },
            onTap = { hitResult ->
                state.onHitGame(hitResult)
            }
        )

        if (!state.isPlaced) {
            Button(
                onClick = {
                    state.placeAnchorVis()
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(text = "Place game")
            }
        }
        
        Text(text = "Score ${state.score}", modifier =  Modifier.align(Alignment.TopCenter))
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