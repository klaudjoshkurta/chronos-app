package com.shkurta.chronos.ui.media

import android.media.session.MediaController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MediaControlBar(state: MediaState, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val controller = remember(state.token) { MediaController(context, state.token) }

    Row(
        modifier = modifier
            .widthIn(max = 320.dp)
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.albumArt?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = state.title,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (state.artist.isNotBlank()) {
                Text(
                    text = state.artist,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        IconButton(
            onClick = { controller.transportControls.skipToPrevious() },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous",
                tint = Color.White, modifier = Modifier.size(20.dp))
        }
        IconButton(
            onClick = {
                if (state.isPlaying) controller.transportControls.pause()
                else controller.transportControls.play()
            },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (state.isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        IconButton(
            onClick = { controller.transportControls.skipToNext() },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(Icons.Filled.SkipNext, contentDescription = "Next",
                tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}
