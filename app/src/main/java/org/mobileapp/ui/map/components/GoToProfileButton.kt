package org.mobileapp.ui.map.components


import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest


@Composable
fun GoToProfileButton(modifier: Modifier, photoUrl: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = modifier, content = {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(photoUrl).crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clip(CircleShape)
                .size(80.dp),
        )
    })
}