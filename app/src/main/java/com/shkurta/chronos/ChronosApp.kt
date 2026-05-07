package com.shkurta.chronos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.shkurta.chronos.ui.clock.ClockScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChronosApp() {
    ClockScreen()
}