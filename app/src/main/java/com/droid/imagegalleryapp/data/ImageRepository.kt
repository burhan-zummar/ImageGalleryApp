package com.droid.imagegalleryapp.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.droid.imagegalleryapp.domain.Image
import com.droid.imagegalleryapp.utils.Constants.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepository(private val context: Context) {

    suspend fun getImages(page: Int): List<Image> {
        // The data fetching logic is now isolated here
        return withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
            )
            val sortOrder = "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"

            val queryArgs = bundleOf(
                ContentResolver.QUERY_ARG_SQL_SORT_ORDER to sortOrder,
                ContentResolver.QUERY_ARG_LIMIT to PAGE_SIZE,
                ContentResolver.QUERY_ARG_OFFSET to page * PAGE_SIZE
            )

            val newImages = mutableListOf<Image>()

            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                queryArgs,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                    )
                    newImages.add(Image(id, name, uri))
                }
            }
            newImages // Return the list of images
        }
    }
}