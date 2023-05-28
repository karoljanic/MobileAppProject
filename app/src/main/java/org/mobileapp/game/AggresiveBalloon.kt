package org.mobileapp.game

import io.github.sceneview.math.Position
import io.github.sceneview.math.toFloat3
import io.github.sceneview.math.toVector3

class AggresiveBalloon(var targetPosition: Position, var speed: Float = 1f) : GameObject() {
    override fun onUpdate(delta: Double) {
        val dir = (targetPosition - position).toVector3().normalized()

        position += dir.toFloat3() * speed
    }
}