package org.mobileapp.games.ballons

import android.content.Context
import android.graphics.Color
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory

class Balloon(
    context: Context,
    var currentPosition: AnchorNode,
    var speed: Vector3,
    var acceleration: Vector3,
    color: Int
) : Node() {

    init {
        MaterialFactory.makeOpaqueWithColor(
            context, com.google.ar.sceneform.rendering.Color(color)
        ).thenAccept { material ->
            renderable = ShapeFactory.makeSphere(0.1f, currentPosition.worldPosition, material)
        }
    }

}