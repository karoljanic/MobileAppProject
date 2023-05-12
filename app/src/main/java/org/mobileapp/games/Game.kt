package org.mobileapp.games

import android.content.Context
import com.google.ar.core.Anchor
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import org.mobileapp.games.ballons.GameObject

abstract class Game(
    protected val session: Session,
    protected val arFragment: ArFragment,
    protected val context: Context,
    protected val origin: Anchor
) : Runnable {
    protected var objects: ArrayList<GameObject> = ArrayList()
    abstract fun start()

    abstract fun pause()

    abstract fun resume()
}