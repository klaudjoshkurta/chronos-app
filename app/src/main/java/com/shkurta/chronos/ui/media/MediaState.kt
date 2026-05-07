package com.shkurta.chronos.ui.media

import android.graphics.Bitmap
import android.media.session.MediaSession

data class MediaState(
    val title: String,
    val artist: String,
    val albumArt: Bitmap?,
    val isPlaying: Boolean,
    val token: MediaSession.Token
)
