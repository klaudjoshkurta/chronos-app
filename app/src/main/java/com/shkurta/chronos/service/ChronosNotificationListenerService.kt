package com.shkurta.chronos.service

import android.app.Notification
import android.content.ComponentName
import android.content.pm.PackageManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.shkurta.chronos.ui.media.MediaBus
import com.shkurta.chronos.ui.media.MediaState
import com.shkurta.chronos.ui.notifications.NotificationAction
import com.shkurta.chronos.ui.notifications.NotificationBus
import com.shkurta.chronos.ui.notifications.NotificationItem

class ChronosNotificationListenerService : NotificationListenerService() {

    private var mediaSessionManager: MediaSessionManager? = null
    private var activeController: MediaController? = null

    private val mediaCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) = updateMediaState()
        override fun onPlaybackStateChanged(state: PlaybackState?) = updateMediaState()
        override fun onSessionDestroyed() {
            clearActiveController()
            refreshSessions()
        }
    }

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { sessions ->
        refreshSessions(sessions)
    }

    // ── Notification listener callbacks ──────────────────────────────────────

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
        NotificationBus.post(NotificationItem(sbn.id, pkg, appName, title, text, sbn.postTime, icon, actions, sbn.key))
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        NotificationBus.remove(sbn.id, sbn.packageName)
    }

    override fun onListenerConnected() {
        NotificationBus.registerCancelCallback { key -> cancelNotification(key) }
        val componentName = ComponentName(this, ChronosNotificationListenerService::class.java)
        mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as? MediaSessionManager
        mediaSessionManager?.addOnActiveSessionsChangedListener(sessionListener, componentName)
        refreshSessions()
    }

    override fun onListenerDisconnected() {
        NotificationBus.unregisterCancelCallback()
        mediaSessionManager?.removeOnActiveSessionsChangedListener(sessionListener)
        clearActiveController()
        MediaBus.update(null)
    }

    // ── Media session helpers ─────────────────────────────────────────────────

    private fun refreshSessions(sessions: List<MediaController>? = null) {
        val componentName = ComponentName(this, ChronosNotificationListenerService::class.java)
        val list = sessions ?: (mediaSessionManager?.getActiveSessions(componentName) ?: emptyList())
        clearActiveController()
        activeController = list.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: list.firstOrNull { it.metadata != null }
        activeController?.registerCallback(mediaCallback)
        updateMediaState()
    }

    private fun updateMediaState() {
        val ctrl = activeController ?: run { MediaBus.update(null); return }
        val metadata = ctrl.metadata ?: run { MediaBus.update(null); return }
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?.takeIf { it.isNotBlank() } ?: run { MediaBus.update(null); return }
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) ?: ""
        val albumArt = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)
        val isPlaying = ctrl.playbackState?.state == PlaybackState.STATE_PLAYING
        MediaBus.update(MediaState(title, artist, albumArt, isPlaying, ctrl.sessionToken))
    }

    private fun clearActiveController() {
        activeController?.unregisterCallback(mediaCallback)
        activeController = null
    }
}
