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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
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

private object Constants {
    val DRAGGABLE_ORIENTATION = Orientation.Vertical
    const val DRAGGABLE_REVERSE_DIRECTION = true
    const val MIN_LONGITUDE_DIFF = 0.00003
    const val MAX_LONGITUDE_DIFF = 0.00015
    const val MIN_LATITUDE_DIFF = 0.00002
    const val MAX_LATITUDE_DIFF = 0.00009
    const val EARTH_ALTITUDE_OFFSET = 1f
    const val ROTATION_X = 0f
    const val ROTATION_Y = 0f
    const val ROTATION_Z = 0f
    val PADDING_DP = 10.dp
    val SCORE_TEXT_FONT_SIZE = 25.sp
    val TIME_TEXT_FONT_SIZE = 25.sp
}

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
                orientation = Constants.DRAGGABLE_ORIENTATION,
                reverseDirection = Constants.DRAGGABLE_REVERSE_DIRECTION,
                onDragStopped = { gameViewModel.game?.onSwipe(0f, it) })
    ) {
        ARScene(modifier = Modifier.fillMaxSize(),
            nodes = gameViewModel.nodes,
            planeRenderer = false,
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

                        if (longDiff in Constants.MIN_LONGITUDE_DIFF..Constants.MAX_LONGITUDE_DIFF &&
                            latDiff in Constants.MIN_LATITUDE_DIFF..Constants.MAX_LATITUDE_DIFF) {
                            // Place the earth anchor at the same altitude as that of the camera to make it easier to view.
                            val altitude = earth.cameraGeospatialPose.altitude - Constants.EARTH_ALTITUDE_OFFSET
                            val rotation = Rotation(Constants.ROTATION_X, Constants.ROTATION_Y, Constants.ROTATION_Z)

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

        val purpleColor = colorResource(id = R.color.purple_700)
        val purpleColorWithAlpha = purpleColor.copy(alpha = 0.5f)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Constants.PADDING_DP),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = purpleColorWithAlpha,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Score:",
                        fontWeight = FontWeight.Bold,
                        fontSize = Constants.SCORE_TEXT_FONT_SIZE,
                        color = Color.LightGray
                    )
                    Text(
                        text = " ${gameViewModel.score}",
                        fontWeight = FontWeight.Normal,
                        fontSize = Constants.SCORE_TEXT_FONT_SIZE,
                        color = Color.White
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = purpleColorWithAlpha,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Time:",
                        fontWeight = FontWeight.Bold,
                        fontSize = Constants.TIME_TEXT_FONT_SIZE,
                        color = Color.LightGray
                    )
                    Text(
                        text = " ${"%.1f".format(gameViewModel.time)}s",
                        fontWeight = FontWeight.Normal,
                        fontSize = Constants.TIME_TEXT_FONT_SIZE,
                        color = Color.White
                    )
                }
            }
        }

    }
}
