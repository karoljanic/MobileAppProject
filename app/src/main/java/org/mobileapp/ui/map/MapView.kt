package org.mobileapp.ui.map

import android.content.Context
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.settings.Settings
import org.mobileapp.tracking.enums.ServiceStatus
import org.mobileapp.viewmodel.MapViewModel
import org.mobileapp.viewmodel.ProfileViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapView(viewModel: MapViewModel = hiltViewModel(),
            navigateToProfileScreen: () -> Unit) {
    AndroidView(
        factory = { context -> org.osmdroid.views.MapView(context) },
        update = { mapView ->
            val mapController = mapView.controller

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
        })

    Button(onClick = navigateToProfileScreen) {

    }
}