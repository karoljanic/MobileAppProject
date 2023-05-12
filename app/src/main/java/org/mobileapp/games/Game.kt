package org.mobileapp.games

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import com.google.ar.core.Anchor
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import org.mobileapp.games.ballons.GameObject

abstract class Game(
    protected val session: Session,
    protected val arFragment: ArFragment,
    protected val context: Context,
    protected val origin: Anchor
) : Runnable, GestureDetector.OnGestureListener {
    protected var objects: ArrayList<GameObject> = ArrayList()

    protected val handler = Handler(Looper.getMainLooper())

    abstract fun start()

    abstract fun pause()

    abstract fun resume()

    protected fun spawnObject(gameObject: GameObject) {
        handler.post {
            arFragment.arSceneView.scene.addChild(gameObject)
            objects.add(gameObject)
        }
    }

    protected fun removeObject(gameObject: GameObject) {
        handler.post {
            arFragment.arSceneView.scene.removeChild(gameObject)
            objects.remove(gameObject)
        }
    }

    override fun onDown(p0: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(p0: MotionEvent) {}

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        return true
    }

    override fun onLongPress(p0: MotionEvent) {}

    override fun onFling(eventDown: MotionEvent, eventUp: MotionEvent, velX: Float, velY: Float): Boolean {
        return true
    }
}