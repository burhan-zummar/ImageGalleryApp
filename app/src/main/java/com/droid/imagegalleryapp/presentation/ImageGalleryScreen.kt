package com.droid.imagegalleryapp.presentation

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
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
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droid.imagegalleryapp.Image
import com.droid.imagegalleryapp.components.ImageItem
import com.droid.imagegalleryapp.components.RememberWindowInfo
import com.droid.imagegalleryapp.components.WindowInfo
import com.droid.imagegalleryapp.utils.Constants.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ImageGalleryScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<ImageViewModel>()
    // Get the context to access the contentResolver
    val context = LocalContext.current
    val images by viewModel.images.collectAsState()
    val gridState = rememberLazyGridState()

    // --- 1. Load the initial page of images ---
    // This LaunchedEffect runs only once when the composable is first displayed.
    LaunchedEffect(Unit) {
        if (images.isEmpty()) { // Only load if the list is empty
            loadNextPage(context, viewModel)
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
            loadNextPage(context, viewModel)
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

private suspend fun loadNextPage(context: Context, viewModel: ImageViewModel) {
    Log.e("ImageGalleryScreen", "loadNextPage: ${viewModel.currentPage}")
    if (!viewModel.canPaginate || viewModel.isLoading) return

    viewModel.isLoading = true
    withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
        )
        val sortOrder = "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"

        val queryArgs = bundleOf(
            ContentResolver.QUERY_ARG_SQL_SORT_ORDER to sortOrder,
            ContentResolver.QUERY_ARG_LIMIT to PAGE_SIZE,
            ContentResolver.QUERY_ARG_OFFSET to viewModel.currentPage * PAGE_SIZE
        )

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            queryArgs,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            val newImages = mutableListOf<Image>()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )
                newImages.add(Image(id, name, uri))
            }
            // Switch back to the main thread to update the UI state
            withContext(Dispatchers.Main) {
                viewModel.appendImages(newImages)
            }
        } ?: viewModel.onLoadFinished() // Handle case where cursor is null
    }
}
