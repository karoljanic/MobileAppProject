package org.mobileapp

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.mobileapp.tracking.enums.ServiceStatus
import org.mobileapp.tracking.track.Track
import org.mobileapp.tracking.utils.LocationUtil
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object MapOverlayBuilder {
    fun createLocationOverlay(context: Context, location: Location, serviceStatus: ServiceStatus): ItemizedIconOverlay<OverlayItem> {
        val overlayItems: ArrayList<OverlayItem> = ArrayList<OverlayItem>()

        val newMarker: Drawable = ContextCompat.getDrawable(context, R.drawable.icon_marker_24)!!

        val overlayItem: OverlayItem =
            createOverlayItem(context, location.latitude, location.longitude, location.accuracy, location.provider!!, location.time)
        overlayItem.setMarker(newMarker)
        overlayItems.add(overlayItem)

        return createOverlay(context, overlayItems)
    }

    fun createTrackOverlay(context: Context, track: Track, serviceStatus: ServiceStatus): Polyline {
        val nodes: MutableList<GeoPoint> = mutableListOf()

        track.trackNodes.forEach { node ->
            nodes.add(GeoPoint(node.getLocation()))
        }

        val polyline = Polyline().apply {
            outlinePaint.color = Color.RED
            outlinePaint.strokeWidth = 5f
            setPoints(nodes)
        }

        return polyline
    }

    private fun createOverlayItem(context: Context, latitude: Double, longitude: Double, accuracy: Float, provider: String, time: Long): OverlayItem {
        val title = "Title"
        val description = "Description"
        val position: GeoPoint = GeoPoint(latitude, longitude)

        val item: OverlayItem = OverlayItem(title, description, position)
        item.markerHotspot = OverlayItem.HotspotPlace.CENTER
        return item
    }

    private fun createOverlay(context: Context, overlayItems: ArrayList<OverlayItem>): ItemizedIconOverlay<OverlayItem> {
        return ItemizedIconOverlay<OverlayItem>(context, overlayItems,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    return false
                }
            })
    }
}