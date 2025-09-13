package app.folderpic.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayer(uri: Uri, currentPage: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    DisposableEffect(uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(currentPage) {
        if (currentPage) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
            exoPlayer.seekTo(0)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
            }
        },
        update = {}
    )
}