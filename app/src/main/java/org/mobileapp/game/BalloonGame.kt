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
import io.github.sceneview.math.Position
import io.github.sceneview.math.toFloat3

class BalloonGame(sceneView: ArSceneView, nodes: MutableList<ArNode>) :
    Game(
        sceneView, nodes,
    ) {

    var currNode: ArNode? = null

    override fun onAnchor() {
    }

    override fun onUpdate(arFrame: ArFrame) {
        val delta = arFrame.time.intervalSeconds
        currNode?.apply {
            rotation += Float3(0f, 10f * delta.toFloat(), 0f)
        }
    }

    override fun onHit(hitResult: HitResult) {
        currNode = GameObject(
            followHitPosition = false,
            instantAnchor = false,
            placementMode = PlacementMode.BEST_AVAILABLE
        ).apply {
            applyPosePosition = false
            applyPoseRotation = false

            loadModelGlbAsync(
                glbFileLocation = "models/Bloon.glb",
//              glbFileLocation = "https://sceneview.github.io/assets/models/Spoons.glb",
                onError = { Log.i("Loading", "$it") },
                onLoaded = { Log.i("Loading", "$it") },
                scaleToUnits = null,
                centerOrigin = Position(y = -1.0f)
            )

            Log.i("Game", "Spawned")

            this.anchor = startingAnchor
            this.position = hitResult.hitPose.position
            this.rotation = hitResult.hitPose.rotation

            this.velocity = Float3(10f, 10f, 10f)

            sceneView.addChild(this)
            nodes.add(this)
        }
    }

    override fun processSwipe(velocityX: Float, velocityY: Float) {
        var dart = GameObject(
            followHitPosition = false,
            instantAnchor = false,
            placementMode = PlacementMode.BEST_AVAILABLE
        ).apply {
            applyPosePosition = false
            applyPoseRotation = false

            loadModelGlbAsync(
                glbFileLocation = "models/Dart.glb",
//              glbFileLocation = "https://sceneview.github.io/assets/models/Spoons.glb",
                onError = { Log.i("Loading", "$it") },
                onLoaded = { Log.i("Loading", "$it") },
                scaleToUnits = null,
                centerOrigin = Position(y = -1.0f)
            )

            Log.i("Game", "Dart Thrown")

            this.anchor = startingAnchor
            this.position = sceneView.cameraNode.position
            this.lookAt(sceneView.cameraNode.screenPointToRay(sceneView.arSession!!.displayWidth.toFloat() / 2.0f,
                sceneView.arSession!!.displayHeight.toFloat() / 2.0f
            ).getPoint(1.0f).toFloat3())

            this.velocity = sceneView.cameraNode.screenPointToRay(sceneView.arSession!!.displayWidth.toFloat() / 2.0f,
                sceneView.arSession!!.displayHeight.toFloat() / 2.0f
            ).getPoint(1.0f).toFloat3() * -velocityY * 100.0f

            sceneView.addChild(this)
            nodes.add(this)
        }

        objects.add(dart)
    }
}