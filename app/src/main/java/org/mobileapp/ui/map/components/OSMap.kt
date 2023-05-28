package org.mobileapp.ui.map.components

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.data.configuration.MapConfig
import org.mobileapp.utils.MapOverlayBuilder
import org.mobileapp.viewmodel.MapViewModel
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

@Composable
fun OSMap(
    viewContext: Context,
    navigateToGameScreen: (String, String, String, Double, Double) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val centerLocation by viewModel.centerLocation
    val zoom by viewModel.mapZoom
    val userLocation by viewModel.userLocation
    val tournaments by viewModel.tournaments
    val stages = viewModel.stages

    AndroidView(factory = { context ->
        val mapView = MapView(context)
        mapView.isTilesScaledToDpi = true
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

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
                viewModel.updateCenterLocation(mapView.projection.currentCenter)
                return false
            }

            override fun onZoom(event: ZoomEvent): Boolean {
                viewModel.updateZoom(event.zoomLevel)
                return false
            }

        })

        mapView
    }) { mapView ->
        mapView.overlays.clear()

        if (centerLocation != null)  {
            mapView.controller.setCenter(centerLocation)
        }

        if (userLocation != null) {
            mapView.overlays.add(
                MapOverlayBuilder.createUserPositionMarker(viewContext, mapView, userLocation!!)
            )
        }

        if (tournaments.data != null && stages.isEmpty()) {
            tournaments.data!!.forEach { tournament ->
                val tournamentCenter = viewModel.calculateTournamentCenter(tournament!!)
                mapView.overlays.add(MapOverlayBuilder.createTournamentPositionMarker(
                    viewContext, mapView, tournamentCenter
                ) {
                    viewModel.chosenTournament(tournament)
                    mapView.controller.animateTo(tournamentCenter)
                })
            }
        }

        if (stages.isNotEmpty()) {
            stages.forEach { stage ->
                mapView.overlays.add(MapOverlayBuilder.createStagePositionMarker(
                    viewContext,
                    mapView,
                    GeoPoint(stage!!.latitude!!, stage.longitude!!)
                ) {
                    viewModel.chosenStage(stage)
                    navigateToGameScreen(
                        stage.id!!,
                        viewModel.userID,
                        stage.gameType!!,
                        stage.latitude!!,
                        stage.longitude
                    )
                })
            }
        }
    }
}