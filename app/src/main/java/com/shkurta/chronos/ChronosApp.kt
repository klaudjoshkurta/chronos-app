package com.shkurta.chronos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.shkurta.chronos.ui.clock.ClockScreen
import com.shkurta.chronos.ui.countdown.CountdownScreen
import com.shkurta.chronos.ui.media.MediaBus
import com.shkurta.chronos.ui.notifications.NotificationBus
import com.shkurta.chronos.ui.notifications.NotificationPermissionGate
import com.shkurta.chronos.ui.timer.TimerScreen

enum class Screen { CLOCK, TIMER, COUNTDOWN }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChronosApp() {
    NotificationPermissionGate {
        var screen by remember { mutableStateOf(Screen.CLOCK) }
        val notifications by NotificationBus.notifications.collectAsState()
        val mediaState by MediaBus.state.collectAsState()

        when (screen) {
            Screen.CLOCK -> ClockScreen(
                onTimerClick = { screen = Screen.TIMER },
                onCountdownClick = { screen = Screen.COUNTDOWN },
                notifications = notifications,
                mediaState = mediaState
            )
            Screen.TIMER -> TimerScreen(onBack = { screen = Screen.CLOCK })
            Screen.COUNTDOWN -> CountdownScreen(onBack = { screen = Screen.CLOCK })
        }
    }
}