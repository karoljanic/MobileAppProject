package org.mobileapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import org.mobileapp.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

object MapOverlayBuilder {
    fun createUserPositionMarker(
        context: Context, mapView: MapView, location: GeoPoint): Marker {
        val markerIcon: Drawable =
            ContextCompat.getDrawable(context, R.drawable.icon_location_marker_24)!!

        val marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.icon = markerIcon
        marker.setOnMarkerClickListener { _, _ -> true }

        return marker
    }

    fun createTournamentPositionMarker(
        context: Context,
        mapView: MapView,
        center: GeoPoint,
        onClick: () -> Unit
    ): Marker {
        val markerIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_event_24)!!

        val marker = Marker(mapView)
        marker.position = center
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.icon = markerIcon
        marker.setOnMarkerClickListener { _, _ ->
            onClick.invoke()
            true
        }

        return marker
    }

    fun createStagePositionMarker(
        context: Context,
        mapView: MapView,
        location: GeoPoint,
        onClick: () -> Unit
    ): Marker {
        val markerIcon: Drawable =
            ContextCompat.getDrawable(context, R.drawable.icon_mystery_location_24)!!

        val marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        marker.icon = markerIcon
        marker.setOnMarkerClickListener { _, _ ->
            onClick.invoke()
            true
        }

        return marker
    }
}