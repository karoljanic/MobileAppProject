package org.mobileapp.game

import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

class GameObject(
    placementMode: PlacementMode = DEFAULT_PLACEMENT_MODE,
    hitPosition: Position = DEFAULT_HIT_POSITION,
    followHitPosition: Boolean = true,
    instantAnchor: Boolean = false
): ArModelNode( placementMode, hitPosition, followHitPosition, instantAnchor) {
    var velocity = Float3()
    var acceleration = Float3()


    fun onCreate() {}
    fun onUpdate(delta: Double) {
        velocity += acceleration * delta.toFloat()
        position += velocity * delta.toFloat()
    }
}