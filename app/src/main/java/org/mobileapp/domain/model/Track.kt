package org.mobileapp.domain.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize
import kotlin.collections.ArrayList
import java.util.Date


@Parcelize
data class Track(
    @Expose var name: String,
    @Expose var trackNodes: ArrayList<TrackNode>,
    @Expose var startDate: Date,
    @Expose var endDate: Date,
    @Expose var stepCount: Float,
    @Expose var trackUriString: String,
    @Expose var gpxUriString: String
) : Parcelable {

    constructor() : this(
        "",
        ArrayList(),
        Date(0),
        Date(0),
        0.0F,
        "",
        ""
    )

    fun getTotalTime(): Long {
        return 0
    }

    fun getTotalLength(): Long {
        return 0
    }
}