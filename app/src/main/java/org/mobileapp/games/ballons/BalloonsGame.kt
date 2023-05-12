package org.mobileapp.games.ballons

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
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

    private lateinit var gestureDetector: GestureDetector

    init {
        spawnObject(
            Balloon(
                context, arFragment.transformationSystem, AnchorNode(origin), Vector3(
                    0.1f,
                    0.2f,
                    0.1f,
                ), Vector3(0f, 0f, 0f), Color.RED
            )
        )

        spawnObject(
            Balloon(
                context, arFragment.transformationSystem, AnchorNode(origin), Vector3(
                    -0.1f,
                    0.2f,
                    0.1f,
                ), Vector3(0f, 0f, 0f), Color.GREEN
            )
        )

        spawnObject(
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

                for (gameObject in objects) {
                    if (gameObject is Arrow) {
                        val overlapped = arFragment.arSceneView.scene.overlapTestAll(gameObject)
                        for (overlappedObject in overlapped) {
                            Log.i("HitArrow", "Hit")
                            removeObject(overlappedObject as GameObject)
                        }
                        if ((gameObject as Arrow).timer >= 5f) {
                            removeObject(gameObject)
                        }
                    }
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

    override fun onLongPress(p0: MotionEvent) {
        Log.i("Gesture", "Long Press")

        val camera = arFragment.arSceneView.scene.camera
        val ray = camera.screenPointToRay(p0.x, p0.y)
        val point = ray.getPoint(1f)

        spawnObject(
            Balloon(
                context, arFragment.transformationSystem, AnchorNode(origin), point, Vector3(0f, 0f, 0f), Color.BLUE
            )
        )
    }

    override fun onFling(eventDown: MotionEvent, eventUp: MotionEvent, velX: Float, velY: Float): Boolean {
        Log.i("Gesture", "Fling")

        val camera = arFragment.arSceneView.scene.camera
        val ray = camera.screenPointToRay(eventUp.x, eventUp.y)
        var point = ray.getPoint(1f)
        point = Vector3(point.x * -velY / 1000f, point.y * -velY / 1000f, point.z * -velY / 1000f)

        spawnObject(
            Arrow(
                context, arFragment.transformationSystem, AnchorNode(origin), point, Vector3(0f, 0f, 0f), Color.BLACK
            )
        )

        return true
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



}