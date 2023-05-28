package org.mobileapp.game

import java.lang.Float.max
import java.lang.Float.min

class FloatingBalloon(var targetHeight : Float, var speed : Float = 0.1f, val score : Int = 100) : GameObject() {
    override fun onUpdate(delta: Double) {
        if (targetHeight > position.y) {
            val deltaHeight =  speed * delta
            position.y = min(position.y + deltaHeight.toFloat(), targetHeight)
        }
        else {
            val deltaHeight =  -speed * delta
            position.y = max(position.y + deltaHeight.toFloat(), targetHeight)
        }
    }
}