package com.phaqlow.stag.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.Songs
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.dao.Tags
import com.phaqlow.stag.ui.home.HomeFragment
import com.phaqlow.stag.ui.playlist.PlaylistFragment
import com.phaqlow.stag.util.ui.LifecycleActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import android.preference.PreferenceManager
import com.phaqlow.stag.util.C


class MainActivity : LifecycleActivity() {
    private val homeFragment = HomeFragment()
    private val playlistFragment = PlaylistFragment()

    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var joins: TagSongJoins

    override fun onCreate(savedInstanceState: Bundle?) {
        checkFirstLaunch()
//        deleteDatabase()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setRxBindings()
//        listData()
    }

    private fun deleteDatabase() {
        applicationContext.deleteDatabase("stag.db")
        finish()
    }

    private fun listData() {
        tagsDb.getAllItems().register {
            Log.d("Stag", "All Tags: $it")
            joins.getAllJoins().register { Log.d("Stag", "All Joins: $it") }
        }
        songsDb.getAllItems().register { Log.d("Stag", "All Songs: $it") }
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
        if (!PreferenceManager.getDefaultSharedPreferences(baseContext)
                        .contains(C.PREF_IS_LAUNCHED_BEFORE)) {
            startActivity(Intent(this, ImportSongsActivity::class.java))
        }
    }
}
