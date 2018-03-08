package com.phaqlow.stag.player

import android.app.Service
import android.content.Intent
import android.os.Binder
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.util.C
import com.phaqlow.stag.util.log
import com.phaqlow.stag.util.ui.LifecycleService
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.ConnectionStateCallback
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.Spotify


class MusicPlayerService : LifecycleService(), ConnectionStateCallback {
    private var spotifyPlayer: Player? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        log("MP service STARTED")

        intent.getStringExtra(C.EXTRA_SPOTIFY_ACCESS_TOKEN)?.let { setupSpotifyPlayer(it) }

        return Service.START_NOT_STICKY
    }

    fun playSong(song: Song) {
        spotifyPlayer?.play(song.uri)
    }

    private fun setupSpotifyPlayer(spotifyAccessToken: String) {
        Spotify.getPlayer(Config(this, spotifyAccessToken, C.SPOTIFY_CLIENT_ID), this,
                object : Player.InitializationObserver {
                    override fun onInitialized(player: Player) {
                        spotifyPlayer = player
                        player.addConnectionStateCallback(this@MusicPlayerService)
                    }
                    override fun onError(error: Throwable) { log("Failed to get Spotify player") }
        })
    }

    // TODO: Add toast/snackbar warnings for failed binding (and if Spotify App is missing)
    override fun onLoggedIn() { }
    override fun onLoggedOut() { }
    override fun onConnectionMessage(message: String) { }
    override fun onLoginFailed(error: Throwable) { }
    override fun onTemporaryError() { }

    private val myBinder = BinderImpl()
    override fun onBind(intent: Intent) = myBinder
    inner class BinderImpl : Binder() {
        fun getService() = this@MusicPlayerService
    }

    override fun onDestroy() {
        log("MP service DESTROYED")
        Spotify.destroyPlayer(this)
        super.onDestroy()
    }
}
