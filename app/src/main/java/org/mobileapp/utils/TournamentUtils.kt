package org.mobileapp.utils

import org.mobileapp.domain.model.TournamentStage
import org.osmdroid.util.GeoPoint

object TournamentUtils {
    fun findCenter(stages: List<TournamentStage?>): GeoPoint {
        var meanLat = 0.0
        var meanLog = 0.0
        stages.forEach {
            meanLat += it!!.latitude!!
            meanLog += it!!.longitude!!
        }

        return GeoPoint(meanLat / stages.size, meanLog / stages.size)
    }
}