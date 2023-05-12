package org.mobileapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.ar.core.Anchor
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.ux.ArFragment
import org.mobileapp.games.Game
import org.mobileapp.games.GameFactory
import org.mobileapp.games.GameType


class GameFragment : Fragment() {
    private var game: Game? = null
    private lateinit var arFragment: ArFragment
    private var gameCreated: Boolean = false
    private var detector: GestureDetector? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ar_core_fragment, container, false).apply {
            arFragment = childFragmentManager.findFragmentById(R.id.ar_scene_view) as ArFragment

            //arFragment.planeDiscoveryController.hide()
            //arFragment.planeDiscoveryController.setInstructionView(null)
            //arFragment.arSceneView.planeRenderer.isVisible = false

            arFragment.arSceneView.scene.addOnUpdateListener {
                arFragment.onUpdate(it)

                val arFrame = arFragment.arSceneView.arFrame ?: return@addOnUpdateListener
                if (arFrame.camera.trackingState != TrackingState.TRACKING) {
                    return@addOnUpdateListener
                }

                val session: Session? = arFragment.arSceneView.session
                if (session != null) {
                    arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
                        if (!gameCreated) {
                            val anchor: Anchor = hitResult.createAnchor()
                            val newGame = GameFactory.build(
                                GameType.BALLOONS, session, arFragment, context, anchor
                            )

                            gameCreated = true
                            newGame.start()

                            detector = GestureDetector(activity, newGame)

                            arFragment.arSceneView.setOnTouchListener { _, event ->
                                Log.i("Gesture", "Touch")
                                detector!!.onTouchEvent(event)
                                true
                            }

                            game = newGame
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        game?.resume()
    }

    override fun onPause() {
        super.onPause()

        game?.pause()
    }
}