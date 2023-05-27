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

    fun anchor(anchor: Anchor) {
        startingAnchor = anchor
        onAnchor()
    }

    protected open fun onAnchor() {}

    open fun update(arFrame: ArFrame) {
        for(obj in objects) {
            obj.onUpdate(arFrame.time.intervalSeconds);
        }
        onUpdate(arFrame)
    }

    protected open fun onUpdate(arFrame: ArFrame) {}

    fun addGameObject(gameObject: GameObject) {
        sceneView.addChild(gameObject)
        nodes.add(gameObject)
        objects.add(gameObject)
    }
    fun deleteGameObject(gameObject: GameObject) {
        nodes.remove(gameObject)
        objects.remove(gameObject)
        gameObject.destroy()
    }

    open fun onHit(hitResult: HitResult) {}
    open fun onSwipe(velocityX: Float, velocityY: Float) {}
}