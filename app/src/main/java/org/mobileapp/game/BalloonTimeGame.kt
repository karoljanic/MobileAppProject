package org.mobileapp.game

import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.ArFrame
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.collision.overlapTest

class BalloonTimeGame(
    sceneView: ArSceneView,
    nodes: MutableList<ArNode>,
    val onScoreChange: (Int) -> Unit,
    val onGameEnd: () -> Unit = {}
) :
    Game(
        sceneView, nodes,
    ) {

    var bloonsLeft = 20
    val bloonsAtOnce = 5
    val spawnInterval = 5.0
    var timeLeftToSpawn = spawnInterval

    val balloons = mutableListOf<GameObject>()
    val darts = mutableListOf<GameObject>()

    override fun onAnchor() {
        onScoreChange(10000)
    }

    override fun onUpdate(arFrame: ArFrame) {
        for (dart in darts) {
            sceneView.overlapTest(dart)?.let { hit ->
                if (hit in balloons) {
                    balloons.remove(hit)
                    deleteGameObject(hit as GameObject)
                }
            }
        }

        onScoreChange((50.0 * arFrame.time.intervalSeconds).toInt())

        timeLeftToSpawn -= arFrame.time.intervalSeconds
        if (timeLeftToSpawn <= 0.0) {
            if (bloonsLeft >= bloonsAtOnce) {
                val newBloons = groupOfBloons(startingAnchor!!.pose.position, 1f, bloonsAtOnce, 0.8f)
                balloons.addAll(newBloons)
                bloonsLeft -= bloonsAtOnce
            }
            else {
                onGameEnd()
            }
            timeLeftToSpawn = spawnInterval
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