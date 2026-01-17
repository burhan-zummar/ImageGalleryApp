package com.droid.imagegalleryapp.presentation

import androidx.lifecycle.ViewModel
import com.droid.imagegalleryapp.Image
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ImageViewModel: ViewModel() {

    private val _images = MutableStateFlow<List<Image>>(emptyList())
    val images = _images.asStateFlow()

    fun updateImages(newImages: List<Image>) {
        _images.value = newImages
    }
}