package org.mobileapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode

@Composable
fun GameView() {
    val nodes = remember { mutableStateListOf<ArNode>() }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                // Apply your configuration
            },
            onSessionCreate = { session ->
                // Configure the ARCore session
            },
            onFrame = { arFrame ->

            },
            onTap = { hitResult ->
                nodes.add(
                    ArModelNode(
                        placementMode = PlacementMode.BEST_AVAILABLE,
                        hitPosition = Position(0.0f, 0.0f, -2.0f),
                        followHitPosition = true,
                        instantAnchor = false
                    )
                )
            }
        )
    }
}