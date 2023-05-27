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
    val onScoreChange: (Int) -> Unit
) :
    Game(
        sceneView, nodes,
    ) {

    val balloons = mutableListOf<GameObject>()
    val darts = mutableListOf<GameObject>()

    override fun onAnchor() {
        val newBloons = groupOfBloons(startingAnchor!!.pose.position, 1f, 10, 0.8f)
        balloons.addAll(newBloons)
    }

    override fun onUpdate(arFrame: ArFrame) {
        for (dart in darts) {
            sceneView.overlapTest(dart)?.let { hit ->
                if (hit in balloons) {
                    balloons.remove(hit)
                    deleteGameObject(hit as GameObject)
                    onScoreChange(100)
                }
            }
        }
    }

//    override fun onHit(hitResult: HitResult) {
//        val balloon = GameObject().apply {
//            loadModel("models/Bloon.glb")
//
//            Log.i("Game", "Spawned")
//
//            this.anchor = startingAnchor
//            this.position = hitResult.hitPose.position
//            this.rotation = hitResult.hitPose.rotation
//
//            //this.velocity = Float3(0f, 1f, 0f)
//
//            addGameObject(this)
//            balloons.add(this)
//        }
//    }

    override fun onSwipe(velocityX: Float, velocityY: Float) {
        val dart = throwDart(velocityY)

        while (darts.size >= 5) {
            val removed = darts.removeFirst()
            deleteGameObject(removed)
        }

        darts.add(dart)
    }
}