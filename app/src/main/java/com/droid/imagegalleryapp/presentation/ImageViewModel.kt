package com.droid.imagegalleryapp.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.droid.imagegalleryapp.Image
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ImageViewModel : ViewModel() {
    companion object {
        private const val TAG = "ImageViewModel"
    }

    private val _images = MutableStateFlow<List<Image>>(emptyList())
    val images = _images.asStateFlow()

    // --- New Pagination State ---
    var currentPage by mutableStateOf(0)
        private set

    var canPaginate by mutableStateOf(true)
        private set

    var isLoading by mutableStateOf(false)

    fun appendImages(newImages: List<Image>) {
        Log.e(TAG, "appendImages: ${newImages.size}")
        if (newImages.isEmpty()) {
            canPaginate = false
        } else {
            _images.update { currentImages ->
                // Add new images and remove duplicates
                (currentImages + newImages).distinctBy { it.id }
            }
            currentPage++ // Increment page number
        }
        isLoading = false
    }

    // To handle end of loading
    fun onLoadFinished() {
        isLoading = false
    }
}