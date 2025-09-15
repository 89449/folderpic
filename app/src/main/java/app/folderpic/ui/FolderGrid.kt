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
import androidx.compose.material3.MaterialTheme
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

import app.folderpic.data.Folder
import app.folderpic.data.MediaLoader
import app.folderpic.util.coilImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderGrid(onFolderClick:(Long, String) -> Unit) {
    val context = LocalContext.current
    var folders by remember { mutableStateOf<List<Folder>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        folders = MediaLoader(context).getMediaFolders()
    }
    Column {
        TopAppBar(
            title = { Text("Folders") }
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(180.dp)
        ) {
            items(folders) { folder ->
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            onFolderClick(folder.id, folder.name)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        AsyncImage(
                            model = coilImageRequest(context, folder.thumbnailUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = folder.count.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    
}
