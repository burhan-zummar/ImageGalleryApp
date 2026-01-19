package com.droid.imagegalleryapp.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droid.imagegalleryapp.data.ImageRepository
import com.droid.imagegalleryapp.presentation.components.ImageItem
import com.droid.imagegalleryapp.presentation.components.RememberWindowInfo
import com.droid.imagegalleryapp.presentation.components.WindowInfo

@Composable
fun ImageGalleryScreen(
    modifier: Modifier = Modifier
) {
    // Get the context to access the contentResolver
    val context = LocalContext.current
    val viewModel = viewModel<ImageViewModel>() {
        ImageViewModel(
            repository = ImageRepository(context)
        )
    }
    val images by viewModel.images.collectAsState()
    val gridState = rememberLazyGridState()

    // --- 1. Load the initial page of images ---
    // This LaunchedEffect runs only once when the composable is first displayed.
    LaunchedEffect(Unit) {
        if (images.isEmpty()) { // Only load if the list is empty
            viewModel.loadNextPage()
        }
    }

    // --- 2. Handle subsequent pagination on scroll ---
    // This derived state correctly calculates when to paginate for a grid.
    val shouldPaginate by remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
            if (lastVisibleItem == null || images.isEmpty()) {
                false
            } else {
                // Trigger when the last visible item is close to the end of the list
                lastVisibleItem.index >= images.size - 5
            }
        }
    }

    // This effect triggers when shouldPaginate becomes true.
    LaunchedEffect(shouldPaginate) {
        if (shouldPaginate && viewModel.canPaginate && !viewModel.isLoading) {
            viewModel.loadNextPage()
        }
    }

    val windowInfo = RememberWindowInfo()

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) 2 else 4),
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(
            items = images,
            key = { image -> image.id }
        ) { image ->
            ImageItem(
                image,
                onClick = { /* Handle image click */ }
            )
        }
    }
}