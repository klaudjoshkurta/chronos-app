package com.shkurta.chronos.ui.notifications

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationBus {
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    @Volatile private var cancelCallback: ((String) -> Unit)? = null

    fun post(item: NotificationItem) {
        val updated = (_notifications.value.toMutableList().also { it.add(0, item) }).take(20)
        _notifications.value = updated
    }

    fun remove(id: Int, packageName: String) {
        _notifications.value = _notifications.value
            .filter { !(it.id == id && it.packageName == packageName) }
    }

    fun registerCancelCallback(callback: (String) -> Unit) { cancelCallback = callback }
    fun unregisterCancelCallback() { cancelCallback = null }
    fun cancelNotification(key: String) { cancelCallback?.invoke(key) }
}
