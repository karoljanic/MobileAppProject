package org.mobileapp.game

import android.util.Log
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.TWO_PI
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.math.Position
import io.github.sceneview.math.toFloat3
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun Game.throwDart(velocity: Float): GameObject = GameObject().apply {
    Log.i("Game", "Dart Thrown")

    loadModel("models/Dart.glb")

    val middleX = sceneView.arSession!!.displayWidth.toFloat() / 2.0f
    val middleY = sceneView.arSession!!.displayHeight.toFloat() / 2.0f
    val ray = sceneView.cameraNode.screenPointToRay(middleX, middleY)
    val dir = ray.direction.toFloat3()
    val origin = ray.getPoint(1.0f).toFloat3()

    Log.i("Ray", "$dir")

    this.anchor = startingAnchor
    this.worldPosition = origin
    this.lookAt(ray.getPoint(1000f).toFloat3())

    val throwSpeed = (velocity / (middleY * 2f)) * 0.2f

    this.velocity = dir * 0.5f
    this.velocity.y += throwSpeed

    this.acceleration.y = -0.1f

    addGameObject(this)
}

fun Game.groupOfBloons(center: Position, radius: Float, count: Int, height: Float) : List<GameObject> {
    val list = mutableListOf<Balloon>()

    for (i in 1..count) {
        Balloon(Random.nextFloat() * height).apply {
            loadModel("models/Bloon.glb")

            Log.i("Game", "Spawned")

            this.anchor = startingAnchor

            val randRadius = Random.nextFloat() * radius
            val randAngle = Random.nextFloat() * TWO_PI

            val x = randRadius * cos(randAngle)
            val y = randRadius * sin(randAngle)

            this.position = center + Float3(x,0f,y)

            this.velocity = Float3(0f, 0.1f, 0f)

            this.velocity.x = (Random.nextFloat() * 2 - 1) / 10f
            this.velocity.z = (Random.nextFloat() * 2 - 1) / 10f

            addGameObject(this)
        }
    }

    return list
}

