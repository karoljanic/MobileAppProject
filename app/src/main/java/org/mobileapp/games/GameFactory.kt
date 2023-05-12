package org.mobileapp.games

import android.content.Context
import com.google.ar.core.Anchor
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import org.mobileapp.games.ballons.BalloonsGame

object GameFactory {
    fun build(
        gameType: GameType,
        session: Session,
        arFragment: ArFragment,
        context: Context,
        origin: Anchor
    ): Game {
        when (gameType) {
            GameType.BALLOONS -> return BalloonsGame(session, arFragment, context, origin)
        }
    }
}