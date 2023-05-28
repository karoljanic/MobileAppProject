package org.mobileapp.game

import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.ArFrame
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.collision.overlapTest
import io.github.sceneview.math.toVector3

class BalloonDefenseGame(
    sceneView: ArSceneView,
    nodes: MutableList<ArNode>,
    val onScoreChange: (Int) -> Unit,
    val onGameEnd: () -> Unit = {}
) :
    Game(
        sceneView, nodes,
    ) {

    var bloonsAtOnce = 5
    var spawnInterval = 5.0
    var timeLeftToSpawn = spawnInterval

    val balloons = mutableListOf<AggresiveBalloon>()
    val darts = mutableListOf<GameObject>()

    override fun onAnchor() {}

    override fun onUpdate(arFrame: ArFrame) {
        for (dart in darts) {
            sceneView.overlapTest(dart)?.let { hit ->
                if (hit is AggresiveBalloon) {
                    balloons.remove(hit)
                    deleteGameObject(hit)
                }
            }
        }

        for (bloon in balloons) {
            if ((bloon.position - startingAnchor!!.pose.position).toVector3().length() <= 1.0f) {
                onGameEnd()
            }
        }

        timeLeftToSpawn -= arFrame.time.intervalSeconds
        if (timeLeftToSpawn <= 0) {
            spawnInterval *= 0.9
            timeLeftToSpawn = spawnInterval

            val bloons = groupOfAggBloons(startingAnchor!!.pose.position, 8.0f, bloonsAtOnce)
            balloons.addAll(bloons)
            onScoreChange(300)
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