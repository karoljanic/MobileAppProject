package org.mobileapp.ui.map


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.StateFlow
import org.mobileapp.data.configuration.MapConfig
import org.mobileapp.domain.model.TournamentState
import org.mobileapp.ui.map.components.CenterMapButton
import org.mobileapp.ui.map.components.GoToProfileButton
import org.mobileapp.viewmodel.MapViewModel
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@Composable
fun MapView(
    viewModel: MapViewModel = hiltViewModel(),
    navigateToProfileScreen: () -> Unit,
    navigateToGameScreen: () -> Unit
) {
    val track by viewModel.track.observeAsState()
    val trackingTime by viewModel.trackingTime.observeAsState()
    val trackingDistance by viewModel.trackingDistance.observeAsState()

    val centerLocation = viewModel.centerLocation
    val userLocation = viewModel.userLocation
    val tournaments = viewModel.tournaments

    Box {
        Map(centerLocation.value,
            userLocation.value,
            viewModel.mapZoom.value,
            updateZoom = { zoom -> viewModel.updateZoom(zoom) },
            updateMapCenter = { mapCenter -> viewModel.updateCenterLocation(mapCenter) },
            tournaments.value)

        GoToProfileButton(
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.BottomStart),
            photoUrl = viewModel.photoUrl
        ) {
            navigateToProfileScreen.invoke()
        }

        CenterMapButton(
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.TopEnd)
        ) {}
    }
}


@Composable
fun Map(
    centerLocation: GeoPoint?,
    userLocation: GeoPoint?,
    zoom: Double,
    updateZoom: (Double) -> Unit,
    updateMapCenter: (GeoPoint) -> Unit,
    tournaments: TournamentState
) {
    AndroidView(factory = { context ->
        val mapView = MapView(context)
        mapView.isTilesScaledToDpi = true
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

        mapView.maxZoomLevel = MapConfig.MAX_MAP_ZOOM
        mapView.minZoomLevel = MapConfig.MIN_MAP_ZOOM

        mapView.setScrollableAreaLimitDouble(
            BoundingBox(
                MapConfig.AREA_LIMITS_NORTH,
                MapConfig.AREA_LIMITS_EAST,
                MapConfig.AREA_LIMITS_SOUTH,
                MapConfig.AREA_LIMITS_WEST
            )
        )

        mapView.controller.setZoom(zoom)

        mapView.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {
                updateMapCenter(mapView.projection.currentCenter)
                return false
            }

            override fun onZoom(event: ZoomEvent): Boolean {
                updateZoom(event.zoomLevel)
                return false
            }

        })

        mapView
    }, update = { mapView ->
        if (centerLocation != null)
            mapView.controller.setCenter(centerLocation)

        if (userLocation != null) {
            val marker = Marker(mapView)
            marker.position = userLocation
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            mapView.overlays.clear()
            mapView.overlays.add(marker)

            mapView.invalidate()
        }

        if(tournaments.data != null) {
            tournaments.data.forEach { t ->
                t!!.stages!!.forEach { s ->
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(s.latitude!!.toDouble(), s.longitude!!.toDouble())
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    mapView.overlays.add(marker)
                }
            }

            mapView.invalidate()
        }
    })
}
