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
        currNode = ArModelNode(
            followHitPosition = false,
            instantAnchor = false,
            placementMode = PlacementMode.BEST_AVAILABLE
        ).apply {
            applyPosePosition = false
            applyPoseRotation = false

            loadModelGlbAsync(
                glbFileLocation = "models/Parrot.glb",
//              glbFileLocation = "https://sceneview.github.io/assets/models/Spoons.glb",
                onError = { Log.i("Loading", "$it") },
                onLoaded = { Log.i("Loading", "$it") },
                scaleToUnits = 0.9f,
                centerOrigin = Position(y = -1.0f)
            )

            Log.i("Game", "Spawned")

            this.anchor = startingAnchor
            this.position = hitResult.hitPose.position
            this.rotation = hitResult.hitPose.rotation
            sceneView.addChild(this)
            nodes.add(this)
        }
    }
}