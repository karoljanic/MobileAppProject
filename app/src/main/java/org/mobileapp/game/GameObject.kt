package org.mobileapp.game

import android.util.Log
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

open class GameObject(): ArModelNode( placementMode = PlacementMode.BEST_AVAILABLE, hitPosition = DEFAULT_HIT_POSITION, followHitPosition = false, instantAnchor = false) {
    var velocity = Float3()
    var acceleration = Float3()

    init {
        applyPosePosition = false
        applyPoseRotation = false
    }

    fun loadModel(path: String) {
        loadModelGlbAsync(
            glbFileLocation = path,
            onError = { Log.i("Loading", "$it") },
            onLoaded = { Log.i("Loading", "$it") },
            scaleToUnits = null,
            centerOrigin = Position(y = -1.0f)
        )
    }

    open fun onUpdate(delta: Double) {
        velocity += acceleration * delta.toFloat()
        position += velocity * delta.toFloat()
    }
}