package org.mobileapp.viewmodel

import android.util.Log
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.ArFrame
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import org.mobileapp.game.BalloonGame
import org.mobileapp.game.Game
import kotlin.math.max

class GameViewModel: ViewModel() {
    var nodes = mutableStateListOf<ArNode>()

    var game by mutableStateOf<Game?>(null)
    var isPlaced by mutableStateOf(false)
    var anchorVis by mutableStateOf<ArModelNode?>(null)
    var score by mutableStateOf(0)

    fun updateGame(arFrame: ArFrame) {
        if (isPlaced) {
            game?.update(arFrame)
        }
    }

    fun onHitGame(hitResult: HitResult) {
        if (isPlaced) {
            Log.i("Game", "tapped")
            game?.onHit(hitResult)
        }
    }

    fun newAnchorVis(arSceneView: ArSceneView) {
        anchorVis = ArModelNode(
            followHitPosition = true,
            placementMode = PlacementMode.BEST_AVAILABLE
        ).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/Pin.glb",
//                      glbFileLocation = "https://sceneview.github.io/assets/models/Spoons.glb",
                onError = { Log.i("Loading", "$it") },
                onLoaded = { Log.i("Loading", "$it") },
                scaleToUnits = null,
                centerOrigin = Position(y = -1.0f)
            )

            arSceneView.addChild(this)
            nodes.add(this)
        }
    }

    fun placeAnchorVis() {
        anchorVis?.let { node ->
            node.anchor()?.let { anchor ->
                anchorGame(anchor)
            }
        }
    }

    fun anchorGame(anchor: Anchor) {
        game?.let { game ->
            game.anchor(anchor)
            isPlaced = true
        }
    }

    fun newGame(arSceneView: ArSceneView) {
        game = BalloonGame(arSceneView, nodes, onScoreChange = {
            val newScore = score + it
            score = max(0, newScore)
        })
    }
}