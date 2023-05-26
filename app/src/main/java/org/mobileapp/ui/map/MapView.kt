package org.mobileapp.ui.map

import android.location.Location
import android.util.Log
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
import org.osmdroid.views.overlay.Marker
import java.util.concurrent.atomic.AtomicBoolean


@Composable
fun MapView(
    viewModel: MapViewModel = hiltViewModel(),
    navigateToProfileScreen: () -> Unit,
    navigateToGameScreen: () -> Unit
) {
    val context = LocalContext.current

    val track by viewModel.track.observeAsState()
    val trackingTime by viewModel.trackingTime.observeAsState()
    val trackingDistance by viewModel.trackingDistance.observeAsState()

    val centerLocation = viewModel.centerLocation
    val userLocation = viewModel.userLocation
    val centerIsSet = viewModel.centerIsSet

    Box {
        Map(centerLocation.value, userLocation.value, centerIsSet.value) {
            viewModel.disableSettingCenter()
        }
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
            onClick = navigateToGameScreen,
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
fun Map(centerLocation: Location?, userLocation: Location?, centerIsSet: Boolean, onCenterSet: () -> Unit) {
    //val mapViewState = rememberMapViewWithLifecycle()

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

        mapView.controller.setZoom(MapConfig.DEFAULT_MAP_ZOOM)

        mapView
    },
    update = { mapView ->
        if(!centerIsSet) {
            if(centerLocation != null) {
                mapView.controller.setCenter(GeoPoint(centerLocation))
                onCenterSet()
            }
        }

        if(userLocation != null) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(userLocation)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            mapView.overlays.add(marker)
        }
    })
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
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver = remember(mapView) {
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            else -> {}
        }
    }
}