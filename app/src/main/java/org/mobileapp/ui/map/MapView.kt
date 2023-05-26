package org.mobileapp.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.mobileapp.data.configuration.MapConfig
import org.mobileapp.data.datastore.MapSettings
import org.mobileapp.viewmodel.MapViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


@Composable
fun MapView(
    viewModel: MapViewModel = hiltViewModel(),
    navigateToProfileScreen: () -> Unit,
    navigateToGameScreen: () -> Unit
) {
    val track by viewModel.track.observeAsState()
    val trackingTime by viewModel.trackingTime.observeAsState()
    val trackingDistance by viewModel.trackingDistance.observeAsState()

    val context = LocalContext.current

    val location = remember { mutableStateOf(runBlocking { MapSettings(context).getLocation.first() }) }
    var locationSet = false
    viewModel.currentLocation.observeForever {
        if(!locationSet) {
            if(location.value.longitude != it.longitude ||
                location.value.latitude != it.latitude) {

                location.value = it
                locationSet = true
            }
        }
    }

    Box {
        Map(onLoad = {
            it.isTilesScaledToDpi = true
            it.setTileSource(TileSourceFactory.MAPNIK)
            it.setMultiTouchControls(true)
            it.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

            it.maxZoomLevel = MapConfig.MAX_MAP_ZOOM
            it.minZoomLevel = MapConfig.MIN_MAP_ZOOM

            it.setScrollableAreaLimitDouble(
                BoundingBox(
                    MapConfig.AREA_LIMITS_NORTH,
                    MapConfig.AREA_LIMITS_EAST,
                    MapConfig.AREA_LIMITS_SOUTH,
                    MapConfig.AREA_LIMITS_WEST
                )
            )

            it.overlays.clear()
            it.controller.setZoom(viewModel.mapZoom(context))
            it.controller.setCenter(GeoPoint(location.value))
        })

        Button(
            shape = CircleShape,
            onClick = navigateToProfileScreen,
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.BottomStart)
                .size(80.dp),
            content = {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(viewModel.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(65.dp),
                )
            }
        )

        Button(
            shape = CircleShape,
            onClick = navigateToGame,
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.BottomEnd)
                .size(80.dp),
            content = {
                Text(text = "Game test")
            }
        )
    }
}

@Composable
fun Map(
    modifier: Modifier = Modifier, onLoad: ((map: MapView) -> Unit)? = null
) {
    val mapViewState = rememberMapViewWithLifecycle()

    AndroidView(
        { mapViewState }, modifier
    ) { mapView -> onLoad?.invoke(mapView) }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context)
    }

    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
    }