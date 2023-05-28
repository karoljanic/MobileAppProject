package org.mobileapp.ui.map.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.mobileapp.R

@Composable
fun CenterMapButton(modifier: Modifier, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = modifier, content = {
        Box(
            modifier = Modifier
                .background(colorResource(R.color.purple_700), CircleShape)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_my_location_24),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(7.dp)
                    .clip(CircleShape)
                    .size(40.dp),
            )
        }
    })
}