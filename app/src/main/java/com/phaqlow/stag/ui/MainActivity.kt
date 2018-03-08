package com.phaqlow.stag.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.player.MusicPlayerService
import com.phaqlow.stag.ui.home.HomeFragment
import com.phaqlow.stag.ui.playlist.PlaylistFragment
import com.phaqlow.stag.util.C
import com.phaqlow.stag.util.hasFlag
import com.phaqlow.stag.util.log
import com.phaqlow.stag.util.ui.LifecycleActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : LifecycleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authenticateWithSpotify()

        initViews()
        setRxBindings()
    }

    private fun authenticateWithSpotify() {
        val request = AuthenticationRequest.Builder(C.SPOTIFY_CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, C.SPOTIFY_LOGIN_REDIRECT_URI)
                .setScopes(arrayOf("playlist-read-private", "streaming"))
                .build()

        AuthenticationClient.openLoginActivity(this, C.SPOTIFY_REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            C.SPOTIFY_REQUEST_CODE -> AuthenticationClient.getResponse(resultCode, data)
                    .takeIf { it.type == AuthenticationResponse.Type.TOKEN }
                    ?.let { response ->
                        bindMusicPlayerService(response.accessToken)
                        importSongsOnFirstLaunch(response.accessToken)
                    }
        }
    }

    private var musicPlayerService: MusicPlayerService? = null
    private var shouldUnbindService = false

    private val musicPlayerConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            musicPlayerService = (service as MusicPlayerService.BinderImpl).getService()
            log("MP service BOUND")
        }

        override fun onServiceDisconnected(className: ComponentName) {
            musicPlayerService = null
            log("MP service DISCONNECTED")
        }
    }

    private fun bindMusicPlayerService(spotifyAccessToken: String) {
        Intent(this, MusicPlayerService::class.java).let { serviceIntent ->
            startService(Intent(serviceIntent).putExtra(C.EXTRA_SPOTIFY_ACCESS_TOKEN, spotifyAccessToken))
            bindService(serviceIntent, musicPlayerConnection, 0)
            shouldUnbindService = true
        }
    }

    private fun unbindMusicPlayerService() {
        if (shouldUnbindService) {
            unbindService(musicPlayerConnection)
            shouldUnbindService = false
        }
    }

    private fun importSongsOnFirstLaunch(spotifyAccessToken: String) {
        if (!hasFlag(C.PREF_IS_LAUNCHED_BEFORE))
            startActivity(Intent(this, ImportSongsActivity::class.java)
                    .putExtra(C.EXTRA_SPOTIFY_ACCESS_TOKEN, spotifyAccessToken))
    }

    private val homeFragment = HomeFragment()
    private val playlistFragment = PlaylistFragment()

    private fun initViews() {
        supportFragmentManager.beginTransaction()
                .add(R.id.content, homeFragment, C.FRAGMENT_HOME)
                .add(R.id.content, playlistFragment, C.FRAGMENT_PLAYLIST)
                .hide(playlistFragment)
                .commit()
    }

    private fun setRxBindings() {
        play_bar.clicks().register { openPlaylistFragment() }
    }

    private fun openPlaylistFragment() {
        if (supportFragmentManager.findFragmentByTag(C.FRAGMENT_PLAYLIST).isHidden) {
            supportFragmentManager.beginTransaction()
                    .hide(homeFragment).show(playlistFragment)
                    .addToBackStack(null).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindMusicPlayerService()
    }
}
