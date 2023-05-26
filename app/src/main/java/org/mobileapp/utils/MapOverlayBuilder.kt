package org.mobileapp.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import org.mobileapp.R
import org.mobileapp.service.enums.ServiceStatus
import org.mobileapp.domain.model.Track
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import java.util.*

object MapOverlayBuilder {
    fun createLocationOverlay(
        context: Context,
        location: Location,
        onSingleTap: (OverlayItem) -> Unit,
        onLongPress: (OverlayItem) -> Unit,
        @ColorInt markerColor: Int
    ): ItemizedIconOverlay<OverlayItem> {
        val overlayItems: ArrayList<OverlayItem> = ArrayList<OverlayItem>()

        val newMarker: Drawable =
            ContextCompat.getDrawable(context, R.drawable.icon_location_marker_24)!!
        DrawableCompat.setTint(newMarker, markerColor)

        val overlayItem: OverlayItem = createOverlayItem(
            context,
            location.latitude,
            location.longitude,
            location.accuracy,
            location.provider!!,
            location.time
        )
        overlayItem.setMarker(newMarker)
        overlayItems.add(overlayItem)

        return createOverlay(context, overlayItems, onSingleTap, onLongPress)
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

    private fun createOverlayItem(
        context: Context,
        latitude: Double,
        longitude: Double,
        accuracy: Float,
        provider: String,
        time: Long
    ): OverlayItem {
        val title = "Title"
        val description = "Description"
        val position: GeoPoint = GeoPoint(latitude, longitude)

        val item: OverlayItem = OverlayItem(title, description, position)
        item.markerHotspot = OverlayItem.HotspotPlace.CENTER
        return item
    }

    private fun createOverlay(
        context: Context, overlayItems: ArrayList<OverlayItem>,
        onSingleTap: (OverlayItem) -> Unit, onLongPress: (OverlayItem) -> Unit
    ): ItemizedIconOverlay<OverlayItem> {
        return ItemizedIconOverlay(
            context,
            overlayItems,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    onSingleTap(item)
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    onLongPress(item)
                    return false
                }
            })
    }
}