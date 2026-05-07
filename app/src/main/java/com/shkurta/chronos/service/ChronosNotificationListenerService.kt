package com.shkurta.chronos.service

import android.app.Notification
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.shkurta.chronos.ui.notifications.NotificationAction
import com.shkurta.chronos.ui.notifications.NotificationBus
import com.shkurta.chronos.ui.notifications.NotificationItem

class ChronosNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return
        if (title.isBlank()) return
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val pkg = sbn.packageName
        val pm = packageManager
        val appName = try {
            pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            pkg
        }
        val icon = try {
            pm.getApplicationIcon(pkg)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        val actions = sbn.notification.actions
            ?.mapNotNull { a ->
                val t = a.title?.toString()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                NotificationAction(t, a.actionIntent)
            }
            ?: emptyList()
        NotificationBus.post(NotificationItem(sbn.id, pkg, appName, title, text, sbn.postTime, icon, actions))
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        NotificationBus.remove(sbn.id, sbn.packageName)
    }
}
