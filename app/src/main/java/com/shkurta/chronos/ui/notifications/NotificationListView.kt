package com.shkurta.chronos.ui.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun NotificationListView(notifications: List<NotificationItem>, modifier: Modifier = Modifier) {
    if (notifications.isEmpty()) return

    var selectedItem by remember { mutableStateOf<NotificationItem?>(null) }
    selectedItem?.let { item ->
        NotificationDetailDialog(item = item, onDismiss = { selectedItem = null })
    }

    Column(
        modifier = modifier.widthIn(max = 320.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        notifications.take(5).forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A), shape = RoundedCornerShape(6.dp))
                    .clickable { selectedItem = item }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.icon?.let { drawable ->
                    Image(
                        painter = rememberDrawablePainter(drawable),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = item.appName,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    maxLines = 1
                )
                Text(
                    text = "·",
                    color = Color(0xFF555555),
                    fontSize = 10.sp
                )
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
