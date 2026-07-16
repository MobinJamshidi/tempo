package com.mobinjam.tempo.core.image

import androidx.compose.runtime.Composable

// platform-specific image picker
@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): ImagePickerLauncher

expect class ImagePickerLauncher {
    fun launch()
}