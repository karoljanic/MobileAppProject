package org.mobileapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import org.mobileapp.game.Game

class GameViewModel: ViewModel() {
    val nodes = mutableStateListOf<ArNode>()
    val game = mutableStateOf<Game?>(null)
    val isPlaced = mutableStateOf(false)
    val anchorVis = mutableStateOf<ArModelNode?>(null)
}