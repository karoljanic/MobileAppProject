package org.mobileapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.mobileapp.R
import org.mobileapp.settings.Settings
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

data class MapLayoutContainer(
    private var context: Context,
    private var container: ViewGroup?,
    private var inflater: LayoutInflater
) {
    var rootView: View = inflater.inflate(R.layout.map_fragment, container, false)
    var myLocation: FloatingActionButton = rootView.findViewById(R.id.my_location)

    private var mapView: MapView = rootView.findViewById(R.id.map)
    private var mapController: IMapController = mapView.controller

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
    }

    fun setLocation(location: GeoPoint, animated: Boolean = false) {
        when (animated) {
            true -> mapController.animateTo(location)
            false -> mapController.setCenter(location)
        }
    }
}

