package com.droid.imagegalleryapp.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droid.imagegalleryapp.data.ImageRepository
import com.droid.imagegalleryapp.domain.Image
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImageViewModel(
    private val repository: ImageRepository
) : ViewModel() {
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

    fun loadNextPage() {
        Log.e(TAG, "loadNextPage: $currentPage")
        if (!canPaginate || isLoading) return
        isLoading = true

        viewModelScope.launch {
            val newImages = repository.getImages(currentPage)
            appendImages(newImages)
            isLoading = false
        }
    }

    // This function is now private as it's an implementation detail
    private fun appendImages(newImages: List<Image>) {
        Log.e(TAG, "appendImages: ${newImages.size}")
        if (newImages.isEmpty()) {
            canPaginate = false
        } else {
            _images.update { currentImages ->
                (currentImages + newImages).distinctBy { it.id }
            }
            currentPage++
        }
    }

    // To handle end of loading
    fun onLoadFinished() {
        isLoading = false
    }
}