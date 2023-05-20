package org.mobileapp.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.mobileapp.R
import org.mobileapp.settings.Settings
import org.mobileapp.tracking.enums.ServiceStatus
import org.mobileapp.tracking.track.Track
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline

@SuppressLint("ClickableViewAccessibility")
class MapLayoutContainer(
    private var context: Context,
    container: ViewGroup?,
    inflater: LayoutInflatercd
) {
    var rootView: View = inflater.inflate(R.layout.map_fragment, container, false)
    var myCurrentLocation: FloatingActionButton = rootView.findViewById(R.id.my_current_location)

    private var mapView: MapView = rootView.findViewById(R.id.map)
    private var mapController: IMapController = mapView.controller

    private var currentPositionOverlay: ItemizedIconOverlay<OverlayItem>
    private var currentTrackOverlay: Polyline

    init {
        mapView.isTilesScaledToDpi = true
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.maxZoomLevel = Settings.getMaxMapZoom()
        mapView.minZoomLevel = Settings.getMinMapZoom()
        mapView.setScrollableAreaLimitDouble(BoundingBox(85.0, 180.0, -85.0, -180.0))
        mapView.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

        mapController.setZoom(Settings.getMapZoom())
        mapController.setCenter(GeoPoint(Settings.getDefaultLocation()))

        currentPositionOverlay = MapOverlayBuilder.createLocationOverlay(
            context,
            Settings.getDefaultLocation(),
            {},
            {},
            Color.RED
        )

        currentTrackOverlay = Polyline()
        mapView.overlays.clear()
        mapView.overlays.add(currentPositionOverlay)


        mapView.setOnTouchListener { _, event ->
            if(event?.action == MotionEvent.ACTION_UP) {
                val projection: Projection = mapView.projection
                val tappedGeoPoint: GeoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint

                Log.i("MAPA","${tappedGeoPoint.latitude} ${tappedGeoPoint.longitude}")
            }

            false
        }
    }

    fun setCurrentLocation(location: Location, animated: Boolean = false) {
        when (animated) {
            true -> mapController.animateTo(GeoPoint(location))
            false -> mapController.setCenter(GeoPoint(location))
        }
    }

    fun markCurrentPosition(location: Location) {
        mapView.overlays.remove(currentPositionOverlay)
        currentPositionOverlay = MapOverlayBuilder.createLocationOverlay(
            context,
            location,
            {},
            {},
            Color.BLUE
        )
        mapView.overlays.add(currentPositionOverlay)
        mapView.invalidate()
    }

    fun markCurrentTrack(track: Track) {
        mapView.overlays.remove(currentTrackOverlay)
        currentTrackOverlay =
            MapOverlayBuilder.createTrackOverlay(context, track, ServiceStatus.IS_NOT_RUNNING)
        mapView.overlays.add(currentTrackOverlay)
    }
}

