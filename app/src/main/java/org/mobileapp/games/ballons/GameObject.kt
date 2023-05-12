package org.mobileapp.games.ballons

import android.content.Context
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

abstract class GameObject() : Node() {
    abstract fun update(dt: Float)
    abstract fun setRenderable()
}