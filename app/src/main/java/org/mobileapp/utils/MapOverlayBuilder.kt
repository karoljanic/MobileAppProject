package org.mobileapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import org.mobileapp.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

object MapOverlayBuilder {
    fun createUserPositionMarker(
        context: Context, mapView: MapView, location: GeoPoint, @ColorInt markerColor: Int
    ): Marker {
        val markerIcon: Drawable =
            ContextCompat.getDrawable(context, R.drawable.icon_location_marker_24)!!
        DrawableCompat.setTint(markerIcon, markerColor)

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
        @ColorInt markerColor: Int,
        onClick: () -> Unit
    ): Marker {
        val markerIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_event_24)!!
        DrawableCompat.setTint(markerIcon, markerColor)


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
        @ColorInt markerColor: Int,
        onClick: () -> Unit
    ): Marker {
        val markerIcon: Drawable =
            ContextCompat.getDrawable(context, R.drawable.icon_mystery_location_24)!!
        DrawableCompat.setTint(markerIcon, markerColor)

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