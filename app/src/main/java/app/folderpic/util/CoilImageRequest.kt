package app.folderpic.util

import android.content.Context
import android.net.Uri
import coil.request.ImageRequest
import coil.request.videoFramePercent

fun coilImageRequest(context: Context, uri: Uri): ImageRequest {
    return ImageRequest.Builder(context)
        .data(uri)
        .videoFramePercent(0.5)
        .build()
}
