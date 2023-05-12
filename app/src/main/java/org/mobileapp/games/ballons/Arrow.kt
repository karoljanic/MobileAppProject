package org.mobileapp.games.ballons

import android.content.Context
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

class Arrow (
    val context: Context,
    transformationSystem: TransformationSystem,
    var anchor: Node,
    var velocity: Vector3,
    var acceleration: Vector3,
    val color: Int
) : GameObject() {
    public var timer = 0f

    init {
        setRenderable()
    }

    override fun update(dt: Float) {
        integrateVelocity(dt)
        integratePosition(dt)

        timer += dt
    }

    override fun setRenderable() {
        MaterialFactory.makeOpaqueWithColor(
            context, com.google.ar.sceneform.rendering.Color(color)
        ).thenAccept { material ->
            renderable = ShapeFactory.makeSphere(0.1f, anchor.localPosition, material)
        }
    }

    private fun integrateVelocity(dt: Float) {
        velocity.x += acceleration.x * dt
        velocity.y += acceleration.y * dt
        velocity.z += acceleration.z * dt
    }

    private fun integratePosition(dt: Float) {
        localPosition = Vector3(
            localPosition.x + velocity.x * dt,
            localPosition.y + velocity.y * dt,
            localPosition.z + velocity.z * dt
        )
    }
}