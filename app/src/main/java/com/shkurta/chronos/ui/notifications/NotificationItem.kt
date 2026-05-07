package com.shkurta.chronos.ui.notifications

import android.app.PendingIntent
import android.graphics.drawable.Drawable

data class NotificationAction(
    val title: String,
    val pendingIntent: PendingIntent?
)

data class NotificationItem(
    val id: Int,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val timestamp: Long,
    val icon: Drawable?,
    val actions: List<NotificationAction> = emptyList()
)
