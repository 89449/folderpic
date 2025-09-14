package app.folderpic.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerUI(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onReplay: () -> Unit,
    onForward: () -> Unit,
    currentPosition: Long,
    duration: Long,
    onSliderChange: (Float) -> Unit
) {
    val sliderPosition = if (duration > 0) {
        currentPosition.toFloat() / duration.toFloat()
    } else {
        0f
    }

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Slider(
                value = sliderPosition,
                onValueChange = onSliderChange,
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(2.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onReplay,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Filled.Replay10, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if(isPlaying) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = if(isPlaying) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onForward,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Filled.Forward10, contentDescription = null)
            }
        }
        Text(
            text = formatTime(currentPosition),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatTime(milliseconds: Long): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
