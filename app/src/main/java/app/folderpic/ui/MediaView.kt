package app.folderpic.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.delay

import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

import app.folderpic.data.MediaItem
import app.folderpic.data.MediaLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaView(folderId: Long, mediaId: Long) {
    val context = LocalContext.current
    var mediaItems by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var isToolbarVisible by rememberSaveable { mutableStateOf(true) }
    var isPlaying by rememberSaveable { mutableStateOf(true) }
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    var videoPosition by remember { mutableStateOf(0L) }
    var videoDuration by remember { mutableStateOf(0L) }
    var isSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(folderId) {
        mediaItems = MediaLoader(context).getMediaForFolder(folderId)
    }

    if (mediaItems.isNotEmpty()) {
        val startingIndex = mediaItems.indexOfFirst { it.id == mediaId }
        val pagerState = rememberPagerState(
            initialPage = if (startingIndex == -1) 0 else startingIndex
        ) {
            mediaItems.size
        }
        val currentItem = mediaItems[pagerState.currentPage]
        
        LaunchedEffect(currentItem.uri) {
            exoPlayer.setMediaItem(ExoMediaItem.fromUri(currentItem.uri))
            exoPlayer.prepare()
        }

        LaunchedEffect(exoPlayer) {
            while (true) {
                if (exoPlayer.isPlaying && !isSeeking) {
                    videoPosition = exoPlayer.currentPosition
                    videoDuration = exoPlayer.duration
                }
                delay(500)
            }
        }
        
        Box(modifier = Modifier.background(Color.Black)) {
            HorizontalPager(state = pagerState) { page ->
                val item = mediaItems[page]
                val zoomState = rememberZoomState()
                if(item.mimeType.startsWith("image")) {
                    AsyncImage(
                        model = item.uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .zoomable(
        	                	zoomState = zoomState,
        	                	onTap = {
        	                	    isToolbarVisible = !isToolbarVisible
        	                	}
    	                	),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    VideoPlayer(
                        currentPage = pagerState.currentPage == page,
                        isPlaying = isPlaying,
                        exoPlayer = exoPlayer,
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { isToolbarVisible = !isToolbarVisible }
                                )
                            }
                    )
                }
            }
            
            if(isToolbarVisible) {
                TopAppBar(
                    title = {},
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                        }
                    },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                if(currentItem.mimeType.startsWith("video/")) {
                    PlayerUI(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        isPlaying = isPlaying,
                        onTogglePlay = { isPlaying = !isPlaying },
                        currentTime = videoPosition,
                        totalDuration = videoDuration,
                        onRewind = { exoPlayer.seekTo(exoPlayer.currentPosition - 10000) },
                        onForward = { exoPlayer.seekTo(exoPlayer.currentPosition + 10000) },
                        onSeekStart = { isSeeking = true },
                        onSeekEnd = {
                            exoPlayer.seekTo(it.toLong())
                            isSeeking = false
                        }
                    )
                }
            }
        }
    }
}
