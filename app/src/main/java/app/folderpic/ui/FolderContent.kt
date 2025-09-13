package app.folderpic.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage

import app.folderpic.data.MediaItem
import app.folderpic.data.MediaLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderContent(
    folderId: Long,
    folderName: String,
    onMediaClick: (Long) -> Unit
) {
    val context = LocalContext.current
    var mediaItems by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    
    LaunchedEffect(folderId) {
        mediaItems = MediaLoader(context).getMediaForFolder(folderId)
    }
    
    Column {
        TopAppBar(
            title = { Text(folderName) }
        )
        LazyVerticalGrid(columns = GridCells.Adaptive(120.dp)) {
            items(mediaItems) { mediaItem ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            onMediaClick(mediaItem.id)
                        }
                ) {
                    AsyncImage(
                        model = mediaItem.uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}