package org.mobileapp.ui.map

import android.content.Context
import android.content.res.Resources.Theme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.mobileapp.R
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
            navigateToProfileScreen: () -> Unit,
            navigateToGame: () -> Unit) {

    Box {
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