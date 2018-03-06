package com.phaqlow.stag.ui

import android.content.Intent
import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.ui.home.HomeFragment
import com.phaqlow.stag.ui.playlist.PlaylistFragment
import com.phaqlow.stag.util.C
import com.phaqlow.stag.util.hasFlag
import com.phaqlow.stag.util.ui.LifecycleActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : LifecycleActivity() {
    private val homeFragment = HomeFragment()
    private val playlistFragment = PlaylistFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        checkFirstLaunch()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setRxBindings()
    }

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

    // TODO: play music as background service

    private fun openPlaylistFragment() {
        if (supportFragmentManager.findFragmentByTag(C.FRAGMENT_PLAYLIST).isHidden) {
            supportFragmentManager.beginTransaction()
                    .hide(homeFragment).show(playlistFragment)
                    .addToBackStack(null).commit()
        }
    }

    private fun checkFirstLaunch() {
        if (!hasFlag(C.PREF_IS_LAUNCHED_BEFORE)) {
            startActivity(Intent(this, ImportSongsActivity::class.java))
        }
    }
}
