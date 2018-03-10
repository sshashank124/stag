package com.phaqlow.stag.ui

import android.content.Intent
import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.player.MusicPlayerServiceConnection
import com.phaqlow.stag.ui.home.HomeFragment
import com.phaqlow.stag.ui.playlist.PlaylistFragment
import com.phaqlow.stag.util.FRAGMENT_HOME
import com.phaqlow.stag.util.FRAGMENT_PLAYLIST
import com.phaqlow.stag.util.PREF_IS_LAUNCHED_BEFORE
import com.phaqlow.stag.util.SPOTIFY_CLIENT_ID
import com.phaqlow.stag.util.SPOTIFY_EXTRA_ACCESS_TOKEN
import com.phaqlow.stag.util.SPOTIFY_LOGIN_REDIRECT_URI
import com.phaqlow.stag.util.SPOTIFY_REQUEST_CODE
import com.phaqlow.stag.util.hasFlag
import com.phaqlow.stag.util.disposables.DisposableActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_main.*


// TODO: try to reuse access token instead of re-authorizing every time
class MainActivity : DisposableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authenticateWithSpotify()

        initViews()
        setRxBindings()
    }

    private fun authenticateWithSpotify() {
        val request = AuthenticationRequest.Builder(SPOTIFY_CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, SPOTIFY_LOGIN_REDIRECT_URI)
                .setScopes(arrayOf("playlist-read-private", "streaming"))
                .build()

        AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SPOTIFY_REQUEST_CODE -> AuthenticationClient.getResponse(resultCode, data)
                    .takeIf { it.type == AuthenticationResponse.Type.TOKEN }
                    ?.let { response ->
                        MusicPlayerServiceConnection.bindService(this, response.accessToken)
                        importSongsOnFirstLaunch(response.accessToken)
                    }
        }
    }

    private fun importSongsOnFirstLaunch(spotifyAccessToken: String) {
        if (!hasFlag(PREF_IS_LAUNCHED_BEFORE))
            startActivity(Intent(this, ImportSongsActivity::class.java)
                    .putExtra(SPOTIFY_EXTRA_ACCESS_TOKEN, spotifyAccessToken))
    }

    private val homeFragment = HomeFragment()
    private val playlistFragment = PlaylistFragment()

    private fun initViews() {
        supportFragmentManager.beginTransaction()
                .add(R.id.content, homeFragment, FRAGMENT_HOME)
                .add(R.id.content, playlistFragment, FRAGMENT_PLAYLIST)
                .hide(playlistFragment)
                .commit()
    }

    private fun setRxBindings() {
        play_bar.clicks().register { openPlaylistFragment() }
    }

    private fun openPlaylistFragment() {
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_PLAYLIST).isHidden) {
            supportFragmentManager.beginTransaction()
                    .hide(homeFragment).show(playlistFragment)
                    .addToBackStack(null).commit()
        }
    }

    override fun onDestroy() {
        MusicPlayerServiceConnection.unbindService(this)
        super.onDestroy()
    }
}
