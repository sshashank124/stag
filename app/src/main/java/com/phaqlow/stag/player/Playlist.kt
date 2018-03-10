package com.phaqlow.stag.player

import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.util.rxcollections.RxVector


// TODO: remove suppression
@Suppress("MemberVisibilityCanBePrivate", "unused")
class Playlist {
    private val playlist = RxVector<Song>()
    private var position = -1

    fun setPlaylist(songs: List<Song>) {
        playlist.setAll(songs)
        resetPosition()
    }

    val currentPosition get() = position

    private fun resetPosition() {
        position = 0
    }
}