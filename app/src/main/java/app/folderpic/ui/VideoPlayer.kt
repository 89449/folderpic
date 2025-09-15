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

import android.util.Log

@Composable
fun VideoPlayer(
    uri: Uri, 
    currentPage: Boolean, 
    isSettled: Boolean,
    isPlaying: Boolean,
    seekAction: Long?,
    seekToPosition: Long?,
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

    LaunchedEffect(currentPage, isSettled, isPlaying) {
        if(currentPage && !isSettled) {
        	exoPlayer.playWhenReady = isPlaying
        } else {
        	exoPlayer.playWhenReady = false
        }
    }
    
    LaunchedEffect(currentPage) {
        if(currentPage) {
            exoPlayer.seekTo(0)
        }
    }
    
    LaunchedEffect(isSettled) {
        if(!isSettled) {
            while (true) {
                onPositionChange(exoPlayer.getCurrentPosition(), exoPlayer.getDuration())
                delay(100)
            }
        }
    }
    
    LaunchedEffect(seekAction) {
        seekAction?.let { seekMillis ->
            val newPosition = (exoPlayer.currentPosition + seekMillis).coerceAtLeast(0)
            exoPlayer.seekTo(newPosition)
        }
    }
    
    LaunchedEffect(seekToPosition) {
        seekToPosition?.let { positionMs ->
            exoPlayer.seekTo(positionMs)
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
