package com.droid.imagegalleryapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.droid.imagegalleryapp.Image

@Composable
fun ImageItem(image: Image) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = image.uri,
            contentDescription = null
        )
        Text(text = image.name)
    }
}