package org.mobileapp.game

import android.util.Log
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.ArFrame
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.rotation
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.collision.overlapTest
import io.github.sceneview.math.Position
import io.github.sceneview.math.toFloat3
import kotlin.math.atan2
import kotlin.random.Random

class BalloonGame(
    sceneView: ArSceneView,
    nodes: MutableList<ArNode>,
    val onScoreChange: (Int) -> Unit,
    val onGameEnd: () -> Unit = {}
) :
    Game(
        sceneView, nodes,
    ) {

    var bloonsLeft = 5
    var timeLeft = 30.0

    val darts = mutableListOf<GameObject>()

    override fun onAnchor() {
        val newBloons = groupOfBloons(startingAnchor!!.pose.position, 1f, bloonsLeft, 0.8f)
    }

    override fun onUpdate(arFrame: ArFrame) {
        for (dart in darts) {
            sceneView.overlapTest(dart)?.let { hit ->
                if (hit is FloatingBalloon) {
                    Log.i("Game", "$dart hit $hit")
                    onScoreChange(hit.score)
                    deleteGameObject(hit)
                    bloonsLeft -= 1
                }
            }
        }

        timeLeft -= arFrame.time.intervalSeconds
        if (timeLeft <= 0) {
            onGameEnd()
        }
    }

    override fun onSwipe(velocityX: Float, velocityY: Float) {
        val dart = throwDart(velocityY)

        while (darts.size >= 5) {
            val removed = darts.removeFirst()
            deleteGameObject(removed)
        }

        darts.add(dart)
    }
}