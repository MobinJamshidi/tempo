package com.mobinjam.tempo.core.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class ImagePickerLauncher {
    actual fun launch() {
        // iOS implementation pending
    }
}

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray) -> Unit): ImagePickerLauncher {
    return remember { ImagePickerLauncher() }
}