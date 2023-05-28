package org.mobileapp.ui.map


import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import org.mobileapp.data.configuration.MapConfig
import org.mobileapp.domain.model.StageState
import org.mobileapp.domain.model.Tournament
import org.mobileapp.domain.model.TournamentPlayer
import org.mobileapp.domain.model.TournamentStage
import org.mobileapp.domain.model.TournamentState
import org.mobileapp.ui.map.components.CenterMapButton
import org.mobileapp.ui.map.components.GoToProfileButton
import org.mobileapp.utils.MapOverlayBuilder
import org.mobileapp.utils.TournamentUtils
import org.mobileapp.viewmodel.MapViewModel
import org.mobileapp.viewmodel.ProfileViewModel
import org.mobileapp.viewmodel.TournamentsViewModel
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
    tViewModel: TournamentsViewModel = hiltViewModel(),
    pViewModel: ProfileViewModel = hiltViewModel(),
    navigateToProfileScreen: () -> Unit,
    navigateToGameScreen: (String, String, String, Double, Double) -> Unit
) {
    val context = LocalContext.current

    val centerLocation = viewModel.centerLocation
    val userLocation = viewModel.userLocation
    val tournaments = viewModel.tournaments
    val allStages = viewModel.allStages
    val stages = viewModel.stages
    val chosenTournament = viewModel.chosenTournament

    Box {
        Map(context,
            centerLocation.value,
            userLocation.value,
            viewModel.mapZoom.value,
            updateZoom = { zoom -> viewModel.updateZoom(zoom) },
            updateMapCenter = { mapCenter -> viewModel.updateCenterLocation(mapCenter) },
            tournaments.value,
            allStages.value,
            stages,
            chosenTournament.value,
            pViewModel.userUID,
            showStages = { t -> viewModel.showStages(t) },
            hideStages = { viewModel.hideStages() },
            navigateToGameScreen = navigateToGameScreen,
            updateTournament = { t -> tViewModel.updateTournament(t) })

        GoToProfileButton(
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.BottomStart),
            photoUrl = viewModel.photoUrl
        ) { navigateToProfileScreen.invoke() }

        CenterMapButton(
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.TopEnd)
        ) { viewModel.centerMap() }
    }
}


@Composable
fun Map(
    currentContext: Context,
    centerLocation: GeoPoint?,
    userLocation: GeoPoint?,
    zoom: Double,
    updateZoom: (Double) -> Unit,
    updateMapCenter: (GeoPoint) -> Unit,
    tournaments: TournamentState,
    allStages: StageState,
    stages: List<TournamentStage?>,
    chosenTournament: Tournament,
    userUID: String,
    showStages: (Tournament) -> Unit,
    hideStages: () -> Unit,
    navigateToGameScreen: (String, String, String, Double, Double) -> Unit,
    updateTournament: (Tournament) -> Unit
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
    }) { mapView ->
        mapView.overlays.clear()

        if (centerLocation != null) mapView.controller.setCenter(centerLocation)

        if (userLocation != null) {
            mapView.overlays.add(
                MapOverlayBuilder.createUserPositionMarker(
                    currentContext, mapView, userLocation, Color.BLUE
                )
            )
        }

        if (tournaments.data != null && stages.isEmpty()) {
            tournaments.data.forEach { tournament ->
                val s = allStages.data!!.filter { it!!.tournamentId == tournament!!.id }
                if (s.isNotEmpty()) {

                    val tournamentCenter = TournamentUtils.findCenter(s)
                    mapView.overlays.add(MapOverlayBuilder.createTournamentPositionMarker(
                        currentContext, mapView, tournamentCenter, Color.YELLOW
                    ) {
                        showStages(tournament!!)
                        mapView.controller.setCenter(tournamentCenter)
                    })
                }
            }
        }

        if (stages.isNotEmpty()) {
            stages.forEach {
                mapView.overlays.add(MapOverlayBuilder.createStagePositionMarker(
                    currentContext,
                    mapView,
                    GeoPoint(it!!.latitude!!, it.longitude!!),
                    Color.MAGENTA
                ) {
                    hideStages()

                    if (it.players == null) {
                        it.players = arrayListOf(TournamentPlayer(userUID, "user", 0))
                    } else {
                        if (!it.players!!.any { user -> user.playerUID == userUID }) {
                            it.players!!.removeAll { user -> user.playerUID == userUID }
                            it.players!!.add(TournamentPlayer(userUID, "user", 0))
                        }
                    }

                    updateTournament(chosenTournament)

                    navigateToGameScreen(it.id!!, userUID, it.gameType!!, it.latitude!!, it.longitude)
                })
            }
        }
    }
}
