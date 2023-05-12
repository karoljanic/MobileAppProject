package org.mobileapp.games.ballons

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.animation.LinearInterpolator
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Vector3Evaluator
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import org.mobileapp.games.Game
import kotlin.random.Random


class BalloonsGame(session: Session, arFragment: ArFragment, context: Context, origin: Anchor) :
    Game(session, arFragment, context, origin) {

    private val gameThread = Thread(this)

    private var isRunning: Boolean = false
    private var isPaused: Boolean = false

    private var balloons: ArrayList<Balloon> = ArrayList()

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var gestureDetector: GestureDetector

    init {
        addBalloon(
            Balloon(
                context, AnchorNode(origin), Vector3(
                    rand(-0.2f, 0.2f),
                    rand(0.2f, 0.2f),
                    rand(0.0f, 0.2f),
                ), Vector3(0f, 0f, 0f), Color.RED
            )
        )

        addBalloon(
            Balloon(
                context, AnchorNode(origin), Vector3(
                    rand(-0.2f, 0.2f),
                    rand(0.2f, 0.2f),
                    rand(0.0f, 0.2f),
                ), Vector3(0f, 0f, 0f), Color.GREEN
            )
        )

        addBalloon(
            Balloon(
                context, AnchorNode(origin), Vector3(
                    rand(-0.2f, 0.2f),
                    rand(-0.2f, 0.2f),
                    rand(0.0f, 0.2f),
                ), Vector3(0f, 0f, 0f), Color.BLUE
            )
        )
    }

    override fun run() {
        var lastTime = System.currentTimeMillis()

        while (isRunning) {
            if (isPaused) {
                continue
            }

            if (arFragment.arSceneView.arFrame!!.camera.trackingState != TrackingState.TRACKING) {
                // end game
                isRunning = false
                break
            }

            val currentTime = System.currentTimeMillis()
            val delta = currentTime - lastTime

            if (delta > 300) {
                lastTime = currentTime
                applyPhysics(balloons, delta.toFloat() / 1000)
            }
        }
    }

    override fun start() {
        isRunning = true

        gameThread.start()
    }

    override fun pause() {
        isRunning = false

        try {
            gameThread.join()
        } catch (_: InterruptedException) {
            Log.e("Error:", "joining thread")
        }
    }

    override fun resume() {
        isRunning = true
        gameThread.start()
    }

    private fun addBalloon(balloon: Balloon) {
        handler.post { arFragment.arSceneView.scene.addChild(balloon.currentPosition) }
        balloon.currentPosition.addChild(balloon)
        balloons.add(balloon)
    }

    private fun applyPhysics(balloons: ArrayList<Balloon>, dt: Float) {
        for (balloon in balloons) {
            integrateSpeed(balloon, dt)
        }

        for (balloon in balloons) {
            handler.post { updatePosition(balloon, dt) }
        }
    }

    private fun integrateSpeed(balloon: Balloon, dt: Float) {
        balloon.speed.x += balloon.acceleration.x * dt
        balloon.speed.y += balloon.acceleration.y * dt
        balloon.speed.z += balloon.acceleration.z * dt
    }

    private fun updatePosition(balloon: Balloon, dt: Float) {
        val newPosition = AnchorNode(
            session.createAnchor(
                Pose(
                    floatArrayOf(
                        balloon.currentPosition.worldPosition.x + balloon.speed.x * dt,
                        balloon.currentPosition.worldPosition.y + balloon.speed.y * dt,
                        balloon.currentPosition.worldPosition.z + balloon.speed.z * dt,
                    ), floatArrayOf(0f, 0f, 0f, 1f)
                )
            )
        )
        newPosition.setParent(arFragment.arSceneView.scene)

        balloon.currentPosition = newPosition

        val objectAnimation = ObjectAnimator()
        objectAnimation.setAutoCancel(true)
        objectAnimation.target = balloon

        objectAnimation.setObjectValues(
            balloon.currentPosition.worldPosition, newPosition.worldPosition
        )

        objectAnimation.setPropertyName("worldPosition")
        objectAnimation.setEvaluator(Vector3Evaluator())
        objectAnimation.interpolator = LinearInterpolator()
        objectAnimation.duration = 300

        objectAnimation.start()


    }

    private fun rand(lowerBound: Float, upperBound: Float): Float {
        return lowerBound + (upperBound - lowerBound) * Random.nextFloat()
    }
}