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
import org.mobileapp.game.BalloonDefenseGame
import org.mobileapp.game.BalloonGame
import org.mobileapp.game.BalloonTimeGame
import org.mobileapp.game.Game
import org.mobileapp.game.GameType.BLOON_ATTACK
import org.mobileapp.game.GameType.BLOON_DEFENSE
import org.mobileapp.game.GameType.BLOON_TIME_ATTACK
import kotlin.math.max

class GameViewModel: ViewModel() {
    var nodes = mutableStateListOf<ArNode>()

    var game by mutableStateOf<Game?>(null)
    var isPlaced by mutableStateOf(false)
    var anchorVis by mutableStateOf<ArModelNode?>(null)
    var score by mutableStateOf(0)
    var time by mutableStateOf(0.0)

    fun updateGame(arFrame: ArFrame) {
        if (isPlaced) {
            time += arFrame.time.intervalSeconds
            game?.update(arFrame)
        }
    }

    fun onHitGame(hitResult: HitResult) {
        if (isPlaced) {
            Log.i("Game", "tapped")
            game?.onHit(hitResult)
        }
    }

    fun anchorGame(anchor: Anchor) {
        game?.let { game ->
            game.anchor(anchor)
            isPlaced = true

            ArModelNode().apply {
                loadModelGlbAsync(
                    glbFileLocation = "models/Pin.glb",
                    onError = { Log.i("Loading", "$it") },
                    onLoaded = { Log.i("Loading", "$it") },
                    scaleToUnits = null,
                    centerOrigin = Position(y = -1.0f)
                )
                this.anchor = game.startingAnchor
                game.sceneView.addChild(this)
                nodes.add(this)
            }
        }
    }

    fun newGame(arSceneView: ArSceneView, gameId: String) {
        val onScoreChange = { scoreChange : Int ->
            val newScore = score + scoreChange
            score = max(0, newScore)
        }

        val onGameEnd = {

        }

        when (gameId) {
            BLOON_ATTACK -> {
                game = BalloonGame(arSceneView, nodes, onScoreChange, onGameEnd)
            }
            BLOON_TIME_ATTACK -> {
                game = BalloonTimeGame(arSceneView, nodes, onScoreChange, onGameEnd)
            }
            BLOON_DEFENSE -> {
                game = BalloonDefenseGame(arSceneView, nodes, onScoreChange, onGameEnd)
            }
        }

    }
}