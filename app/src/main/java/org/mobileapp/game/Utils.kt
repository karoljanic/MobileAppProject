package org.mobileapp.game

import android.util.Log
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.collision.Sphere
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.TWO_PI
import io.github.sceneview.math.Position
import io.github.sceneview.math.toFloat3
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object GameType {
    const val BLOON_ATTACK = "Balloon Attack"
    const val BLOON_TIME_ATTACK = "Balloon Time Attack"
    const val BLOON_DEFENSE = "Balloon Defense"
}

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

    this.velocity = dir * 1.5f
    this.velocity.y += throwSpeed

    this.acceleration.y = -0.1f

    //collisionShape = Sphere(5.0f)

    addGameObject(this)
}

fun Game.groupOfBloons(center: Position, maxRadius: Float, count: Int, maxHeight: Float) : List<FloatingBalloon> {
    val list = mutableListOf<FloatingBalloon>()

    for (i in 1..count) {
        val isSpecial = (i % 5 == 0)
        val score = if (isSpecial) {500} else {100}

        FloatingBalloon(Random.nextFloat() * maxHeight, score = score).apply {
            if (isSpecial) {
                loadModel("models/BloonYellow.glb")
            }
            else {
                loadModel("models/Bloon.glb")
            }

            //collisionShape = Sphere(10.0f)

            Log.i("Game", "Spawned")

            this.anchor = startingAnchor

            val randRadius = Random.nextFloat() * maxRadius
            val randAngle = Random.nextFloat() * TWO_PI

            val x = randRadius * cos(randAngle)
            val y = randRadius * sin(randAngle)

            this.position = center + Float3(x,0f,y)

            this.velocity = Float3(0f, 0.1f, 0f)

            this.velocity.x = (Random.nextFloat() * 2 - 1) / 10f
            this.velocity.z = (Random.nextFloat() * 2 - 1) / 10f

            addGameObject(this)

            list.add(this)
        }
    }

    return list
}

fun Game.groupOfAggBloons(target: Position, radius: Float, count: Int) : List<AggresiveBalloon> {
    val list = mutableListOf<AggresiveBalloon>()

    for (i in 1..count) {
        AggresiveBalloon(target).apply {
            loadModel("models/Bloon.glb")

            Log.i("Game", "Spawned")

            this.anchor = startingAnchor

            val randAngle = Random.nextFloat() * TWO_PI

            val x = radius * cos(randAngle)
            val y = radius * sin(randAngle)

            this.position = targetPosition + Float3(x,0f,y)

            addGameObject(this)

            list.add(this)
        }
    }

    return list
}

