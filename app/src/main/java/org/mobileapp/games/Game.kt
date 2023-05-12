package org.mobileapp.games

import android.content.Context
import com.google.ar.core.Anchor
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

abstract class Game(
    protected val session: Session,
    protected val arFragment: ArFragment,
    protected val context: Context,
    protected val origin: Anchor
) : Runnable {
    abstract fun start()

    abstract fun pause()

    abstract fun resume()
}