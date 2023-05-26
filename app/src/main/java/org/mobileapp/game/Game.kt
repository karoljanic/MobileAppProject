package org.mobileapp.game

import android.util.Log
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.ArFrame
import io.github.sceneview.ar.node.ArNode

abstract class Game(val sceneView: ArSceneView, val nodes : MutableList<ArNode>) {
    var objects = mutableListOf<GameObject>()
    init {
        Log.i("Game", "Created")
    }

    var startingAnchor: Anchor? = null
    var isRunning: Boolean = false

    fun start() {
        isRunning = true
        onStart()
    }

    open fun onStart() {}

    fun anchor(anchor: Anchor) {
        startingAnchor = anchor
        onAnchor()
    }

    open fun onAnchor() {}

    open fun onUpdate(arFrame: ArFrame) {
        for(obj in objects) {
            obj.onUpdate(arFrame.time.intervalSeconds);
        }
    }

    open fun onHit(hitResult: HitResult) {}
    abstract fun processSwipe(velocityX: Float, velocityY: Float)
}