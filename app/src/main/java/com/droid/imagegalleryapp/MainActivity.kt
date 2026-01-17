package com.droid.imagegalleryapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.droid.imagegalleryapp.components.ReadMediaPermission
import com.droid.imagegalleryapp.presentation.ImageGalleryScreen
import com.droid.imagegalleryapp.ui.theme.ImageGalleryAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageGalleryAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    var showImageGalleryScreen by remember { mutableStateOf(false) }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (!showImageGalleryScreen) {
                            ReadMediaPermission(
                                onPermissionGranted = { showImageGalleryScreen = true }
                            )
                        } else {
                            ImageGalleryScreen(
                                modifier = Modifier.padding(innerPadding),
                            )
                        }
                    } else {
                        ImageGalleryScreen(
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}