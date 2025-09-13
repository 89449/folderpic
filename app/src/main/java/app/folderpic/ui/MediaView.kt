package app.folderpic.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

import app.folderpic.data.MediaItem
import app.folderpic.data.MediaLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaView(folderId: Long, mediaId: Long) {
    val context = LocalContext.current
    var mediaItems by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var isToolbarVisible by remember { mutableStateOf(true) }
    
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
        
        Box {
            HorizontalPager(state = pagerState) { page ->
                val item = mediaItems[page]
                val zoomState = rememberZoomState(contentSize)
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
                        uri = item.uri,
                        currentPage = pagerState.currentPage == page,
                        modifier = Modifier.fillMaxSize()
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
            }
        }
    }
}