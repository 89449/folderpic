package app.folderpic.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun VideoPlayer(
    uri: Uri, 
    currentPage: Boolean, 
    isPlaying: Boolean,
    seekAction: Long?,
    onSeekHandled: () -> Unit,
    seekToPosition: Long?,
    onSeekToPositionHandled: () -> Unit,
    currentPosition: Long,
    onPositionChange: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val updatedIsPlaying by rememberUpdatedState(isPlaying)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            setMediaItem(MediaItem.fromUri(uri)) 
            prepare()
        }
    }

    LaunchedEffect(currentPage, isPlaying) {
        exoPlayer.playWhenReady = currentPage && isPlaying
        
        if (!currentPage) {
            exoPlayer.seekTo(0)
        }
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            onPositionChange(exoPlayer.getCurrentPosition(), exoPlayer.getDuration())
            delay(100)
        }
    }
    
    LaunchedEffect(seekAction) {
        seekAction?.let { seekMillis ->
            val newPosition = (exoPlayer.currentPosition + seekMillis).coerceAtLeast(0)
            exoPlayer.seekTo(newPosition)
            onSeekHandled()
        }
    }
    
    LaunchedEffect(seekToPosition) {
        seekToPosition?.let { positionMs ->
            exoPlayer.seekTo(positionMs)
            onSeekToPositionHandled()
        }
    }


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (updatedIsPlaying) {
                        exoPlayer.play()
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            exoPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(it).apply {
                useController = false
                player = exoPlayer
            }
        }
    )
}
