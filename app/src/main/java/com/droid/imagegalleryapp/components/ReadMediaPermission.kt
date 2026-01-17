package com.droid.imagegalleryapp.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun ReadMediaPermission(
    onPermissionGranted: () -> Unit,
) {
    val context = LocalContext.current

    // Launcher to request read media permission
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Read Media permission is required", Toast.LENGTH_LONG).show()
        }
    }

    // Check current permission state
    val permissionStatus = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_MEDIA_IMAGES
    ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(Unit) {
        if (!permissionStatus) {
            launcher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            onPermissionGranted()
        }
    }

    // UI when permission not granted yet
    if (!permissionStatus) {
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Requesting Read Media Permission...")
        }
    }
}