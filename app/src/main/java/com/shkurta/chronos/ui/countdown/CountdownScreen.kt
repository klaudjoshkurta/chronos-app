package com.shkurta.chronos.ui.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shkurta.chronos.ui.clock.KeepScreenOn
import kotlinx.coroutines.delay

private val Presets = listOf(
    "1 min" to 60L,
    "5 min" to 300L,
    "10 min" to 600L,
    "25 min" to 1500L
)

private val AccentOrange = Color(0xFFFF6B35)

@Composable
fun CountdownScreen(onBack: () -> Unit) {
    var selectedDuration by remember { mutableLongStateOf(0L) }
    var remainingSeconds by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var presetPicked by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
            if (remainingSeconds == 0L) {
                isRunning = false
                isFinished = true
            }
        }
    }

    KeepScreenOn()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text("← Clock", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!presetPicked) {
                Text("Select duration", color = Color.Gray, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Presets.forEach { (label, seconds) ->
                        TextButton(
                            onClick = {
                                selectedDuration = seconds
                                remainingSeconds = seconds
                                isFinished = false
                                presetPicked = true
                            },
                            modifier = Modifier.border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(50)
                            )
                        ) {
                            Text(label, color = Color.White, fontSize = 15.sp)
                        }
                    }
                }
            } else {
                val displayColor = if (isFinished) AccentOrange else Color.White
                Text(
                    text = formatRemaining(remainingSeconds),
                    fontSize = 64.sp,
                    color = displayColor,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    if (!isFinished) {
                        TextButton(onClick = { isRunning = !isRunning }) {
                            Text(
                                text = if (isRunning) "Pause" else "Start",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
                    TextButton(onClick = {
                        isRunning = false
                        isFinished = false
                        presetPicked = false
                        remainingSeconds = 0L
                        selectedDuration = 0L
                    }) {
                        Text("Reset", color = Color.Gray, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

private fun formatRemaining(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s)
    else "%02d:%02d".format(m, s)
}
