package com.shkurta.chronos.ui.clock

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.shkurta.chronos.ui.media.MediaControlBar
import com.shkurta.chronos.ui.media.MediaState
import com.shkurta.chronos.ui.notifications.NotificationItem
import com.shkurta.chronos.ui.notifications.NotificationListView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockScreen(
    onTimerClick: () -> Unit = {},
    onCountdownClick: () -> Unit = {},
    notifications: List<NotificationItem> = emptyList(),
    mediaState: MediaState? = null
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000L)
        }
    }

    KeepScreenOn()

    ClockDisplay(
        time = currentTime,
        onTimerClick = onTimerClick,
        onCountdownClick = onCountdownClick,
        notifications = notifications,
        mediaState = mediaState
    )
}

@Composable
fun KeepScreenOn() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as Activity).window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
fun ClockDisplay(
    time: LocalTime,
    onTimerClick: () -> Unit = {},
    onCountdownClick: () -> Unit = {},
    notifications: List<NotificationItem> = emptyList(),
    mediaState: MediaState? = null
) {
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm:ss") }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMMM d") }
    val date = remember(time) { LocalDate.now() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = time.format(formatter),
                fontSize = 64.sp,
                color = Color.White,
                fontWeight = FontWeight.Light,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = date.format(dateFormatter),
                fontSize = 18.sp,
                color = Color.Gray
            )
            mediaState?.let { state ->
                Spacer(modifier = Modifier.height(12.dp))
                MediaControlBar(state = state)
            }
            if (notifications.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                NotificationListView(notifications = notifications)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            TextButton(onClick = onTimerClick) {
                Text("Timer", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
            TextButton(onClick = onCountdownClick) {
                Text("Countdown", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
        }
    }
}