package com.phaqlow.stag.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.support.v4.app.NotificationCompat
import com.phaqlow.stag.R
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.ui.MainActivity
import com.phaqlow.stag.util.NOTIFICATION_CHANNEL_ID
import com.phaqlow.stag.util.NOTIFICATION_CHANNEL_NAME
import com.phaqlow.stag.util.NOTIFICATION_ID
import com.phaqlow.stag.util.SPOTIFY_CLIENT_ID
import com.phaqlow.stag.util.SPOTIFY_EXTRA_ACCESS_TOKEN
import com.phaqlow.stag.util.disposables.DisposableService
import com.phaqlow.stag.util.log
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.Error
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerEvent
import com.spotify.sdk.android.player.Spotify
import com.spotify.sdk.android.player.SpotifyPlayer


/*
MainActivity -> startForeground
Playback finishes -> stopSelf
 */


// TODO: remove suppression
@Suppress("MemberVisibilityCanBePrivate")
// TODO: consider looking at other callbacks for player state
class MusicPlayerService : DisposableService(), Player.NotificationCallback {
    private var spotifyPlayer: SpotifyPlayer? = null
    val playlist = Playlist()

    // region [ MUSIC CONTROL ]
    fun playSongs(vararg songs: Song) = playSongs(songs.asList())
    fun playSongs(songs: List<Song>) {
        playlist.setPlaylist(songs)
        // this supposedly resets the context which means it will clear the queue
        spotifyPlayer?.playUri(null, songs.first().uri, 0, 0)
    }
    // endregion

    override fun onPlaybackEvent(playerEvent: PlayerEvent) {
        log("MP playback event: $playerEvent")
    }

    override fun onPlaybackError(error: Error) {
        log("MP playback error: $error")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        log("MP service STARTED")

        makeForeground()

        Handler().postDelayed({ stopSelf() }, 10000L)

        intent.getStringExtra(SPOTIFY_EXTRA_ACCESS_TOKEN)?.let { setupSpotifyPlayer(it) }

        return Service.START_NOT_STICKY
    }

    private fun setupSpotifyPlayer(spotifyAccessToken: String) {
        Spotify.getPlayer(Config(this, spotifyAccessToken, SPOTIFY_CLIENT_ID), this,
                object : SpotifyPlayer.InitializationObserver {
                    override fun onInitialized(player: SpotifyPlayer) {
                        spotifyPlayer = player
                        player.addNotificationCallback(this@MusicPlayerService)
                    }
                    override fun onError(error: Throwable) { log("Failed to get Spotify player") }
                })
    }

    // TODO: Add toast/snackbar warnings for failed binding (and if Spotify App is missing)

    private fun makeForeground() {
        initNotification()
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Stag")
                .setContentText("Music Player")
                .setSmallIcon(R.drawable.img_stag)
                .setContentIntent(pendingIntent)
                .setTicker("Stag Music Player")
                .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(NotificationChannel(NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT))
        }
    }

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
