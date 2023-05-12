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
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Vector3Evaluator
import com.google.ar.sceneform.ux.ArFragment
import org.mobileapp.games.Game


class BalloonsGame(session: Session, arFragment: ArFragment, context: Context, origin: Anchor) :
    Game(session, arFragment, context, origin) {

    private val gameThread = Thread(this)

    private var isRunning: Boolean = false
    private var isPaused: Boolean = false

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var gestureDetector: GestureDetector

    init {
        spawnBalloon(
            Balloon(
                context, arFragment.transformationSystem, AnchorNode(origin), Vector3(
                    0.1f,
                    0.2f,
                    0.1f,
                ), Vector3(0f, 0f, 0f), Color.RED
            )
        )

        spawnBalloon(
            Balloon(
                context, arFragment.transformationSystem, AnchorNode(origin), Vector3(
                    -0.1f,
                    0.2f,
                    0.1f,
                ), Vector3(0f, 0f, 0f), Color.GREEN
            )
        )

        spawnBalloon(
            Balloon(
                context, arFragment.transformationSystem, AnchorNode(origin), Vector3(
                    -0.1f,
                    0.2f,
                    -0.1f,
                ), Vector3(0f, 0f, 0f), Color.BLUE
            )
        )
    }

    override fun run() {
        var lastTime = System.currentTimeMillis()
        val targetTime = 1000.0 / 60 // target time in milliseconds for each frame

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

            lastTime = currentTime
            handler.post {
                for (balloon in objects) {
                    balloon.update(delta.toFloat() / 1000)
                }
            }

            // Calculate how long this frame took
            val frameTime = System.currentTimeMillis() - currentTime

            // If it was faster than our target time, delay to achieve 60 FPS cap
            if (frameTime < targetTime) {
                Thread.sleep((targetTime - frameTime).toLong())
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

    private fun spawnBalloon(balloon: Balloon) {
        handler.post { arFragment.arSceneView.scene.addChild(balloon) }
        objects.add(balloon)
    }
}