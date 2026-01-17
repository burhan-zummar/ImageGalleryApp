package com.droid.imagegalleryapp.presentation

import android.content.ContentUris
import android.provider.MediaStore
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droid.imagegalleryapp.Image
import com.droid.imagegalleryapp.components.ImageItem

@Composable
fun ImageGalleryScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<ImageViewModel>()

    // Get the context to access the contentResolver
    val context = LocalContext.current

    // LaunchedEffect will run this block when the composable enters the Composition
    // and will cancel and relaunch if any of its keys change.
    // Using `Unit` as a key means it will only run once.
    LaunchedEffect(Unit) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
        )

        /*
        If you are looking for specific time frame, than add selection and selectionArgs in query
        val millisYesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
        val selectionArgs = arrayOf(millisYesterday.toString())*/

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        // Perform the query in a background-safe way within the coroutine scope
        // provided by LaunchedEffect.
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            val images = mutableListOf<Image>()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                images.add(Image(id, name, uri))
            }
            // Update the ViewModel with the new list of images
            viewModel.updateImages(images)
        }
    }

    val images by viewModel.images.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(images.size) {
        listState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = images,
            key = { image -> image.id }
        ) { image ->
            ImageItem(image)
        }
    }
}