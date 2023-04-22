package org.mobileapp

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.mobileapp.settings.Settings
import org.mobileapp.tracking.enums.ServiceStatus
import org.mobileapp.tracking.track.Track
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay

class MapLayoutContainer(
    private var context: Context,
    private var container: ViewGroup?,
    private var inflater: LayoutInflater
) {
    var rootView: View = inflater.inflate(R.layout.map_fragment, container, false)
    var myLocation: FloatingActionButton = rootView.findViewById(R.id.my_location)

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

        mapView.overlays.clear()

        currentPositionOverlay = MapOverlayBuilder.createLocationOverlay(context, Settings.getDefaultLocation(), ServiceStatus.IS_NOT_RUNNING)
        mapView.overlays.add(currentPositionOverlay)

        currentTrackOverlay = Polyline()
    }

    fun setLocation(location: Location, animated: Boolean = false) {
        when (animated) {
            true -> mapController.animateTo(GeoPoint(location))
            false -> mapController.setCenter(GeoPoint(location))
        }
    }

    fun markCurrentPosition(location: Location) {
        mapView.overlays.remove(currentPositionOverlay)
        currentPositionOverlay = MapOverlayBuilder.createLocationOverlay(context, location, ServiceStatus.IS_NOT_RUNNING)
        mapView.overlays.add(currentPositionOverlay)
    }

    fun markCurrentTrack(track: Track) {
        mapView.overlays.remove(currentTrackOverlay)
        currentTrackOverlay = MapOverlayBuilder.createTrackOverlay(context, track, ServiceStatus.IS_NOT_RUNNING)
        mapView.overlays.add(currentTrackOverlay)
    }
}

