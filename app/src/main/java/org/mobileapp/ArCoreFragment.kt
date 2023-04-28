package org.mobileapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class ArCoreFragment : Fragment() {
    private lateinit var arSceneView: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("test", "ok")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ar_core_fragment, container, false).apply {
            arSceneView = childFragmentManager.findFragmentById(R.id.ar_scene_view) as ArFragment
            arSceneView.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
                Log.i("test", "test")
//                if (plane.getType() == Plane.Type.HORIZONTAL_UPWARD_FACING) {
                    val anchor = AnchorNode(hitResult.createAnchor())
                    val colorRed = com.google.ar.sceneform.rendering.Color(Color.RED)
                    MaterialFactory.makeOpaqueWithColor(context, colorRed).thenAccept { material ->
                        val cube = ShapeFactory.makeCube(
                            Vector3(0.1f, 0.1f, 0.1f),
                            Vector3(0f, -0.4f, 0f),
                            material
                        )

                        val node = TransformableNode(arSceneView.transformationSystem).apply {
                            renderable = cube
                            setParent(anchor)
                        }

                        arSceneView.arSceneView.scene.addChild(node)
                        node.select()
                    }
//                }
            }
        }
    }
}

//    private fun onUpdate() {
//
//        Log.i("test", "dziala")
//
//        val session: Session = arSceneView.session ?: return
//
//        val config = Config(session)
//        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
//        session.configure(config)
//
//        if (arSceneView.session == null) {
//            return
//        }
//
//        val cube = ShapeFactory.makeCube(
//            Vector3(0f, 0f, -1f),
//            Vector3(0.1f, 0.1f, 0.1f),
//            MaterialFactory.makeOpaqueWithColor(context,
//                com.google.ar.sceneform.rendering.Color(Color.RED)
//            ).get()
//        )
//
//        val node = Node().apply {
//            renderable = cube
//            localPosition = Vector3(0f, 0f, -1f)
//        }
//
//        arSceneView.scene.addChild(node)
//    }
