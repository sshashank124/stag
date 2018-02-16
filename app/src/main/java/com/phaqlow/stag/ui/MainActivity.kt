package com.phaqlow.stag.ui

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.dao.Songs
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.ui.home.HomeFragment
import com.phaqlow.stag.ui.playlist.PlaylistFragment
import com.phaqlow.stag.util.ui.LifecycleActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : LifecycleActivity() {
    private val homeFragment = HomeFragment()
    private val playlistFragment = PlaylistFragment()

    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var tagSongJoinsDb: TagSongJoins

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setRxBindings()
        //createTestData()
        listData()
    }

    private fun listData() {
        tagsDb.getAllItems().register { tags ->
            Log.d("Stag", "All Tags: $tags")
            tagSongJoinsDb.getAllJoins().register { tsjs ->
                Log.d("Stag", "All Joins: $tsjs")
            }
        }
        songsDb.getAllItems().register { songs ->
            Log.d("Stag", "All Songs: $songs")
        }
    }

    private val tagIds = SparseArray<Long>()
    private val songIds = SparseArray<Long>()

    private fun createTestData() {
        fun createJoins() {
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(1), songIds.get(1)).register()
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(1), songIds.get(3)).register()
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(1), songIds.get(5)).register()

            tagSongJoinsDb.insertTagSongJoin(tagIds.get(2), songIds.get(2)).register()
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(2), songIds.get(4)).register()
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(2), songIds.get(6)).register()

            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(1)).register()
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(2)).register()
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(3)).register()
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(4)).register()
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(5)).register()
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(6)).register()
        }

        fun tagAdded(tagId: Long, idx: Int) {
            tagIds.put(idx, tagId)
            if (tagIds.size() == 3 && songIds.size()== 6) createJoins()
        }

        fun songAdded(songId: Long, idx: Int) {
            songIds.put(idx, songId)
            if (tagIds.size() == 3 && songIds.size() == 6) createJoins()
        }

        tagsDb.insertItem(Tag("T3")).register { id -> tagAdded(id, 1) }
        tagsDb.insertItem(Tag("T2")).register { id -> tagAdded(id, 2) }
        tagsDb.insertItem(Tag("T1")).register { id -> tagAdded(id, 3) }

        songsDb.insertItem(Song("S1")).register { id -> songAdded(id, 1) }
        songsDb.insertItem(Song("S6")).register { id -> songAdded(id, 2) }
        songsDb.insertItem(Song("S5")).register { id -> songAdded(id, 3) }
        songsDb.insertItem(Song("S4")).register { id -> songAdded(id, 4) }
        songsDb.insertItem(Song("S2")).register { id -> songAdded(id, 5) }
        songsDb.insertItem(Song("S3")).register { id -> songAdded(id, 6) }
    }

    private fun initViews() {
        supportFragmentManager.beginTransaction()
                .add(R.id.content, homeFragment, HomeFragment.TAG)
                .add(R.id.content, playlistFragment, PlaylistFragment.TAG)
                .hide(playlistFragment)
                .commit()
    }

    private fun setRxBindings() {
        play_bar.clicks().register { openPlaylistFragment() }
    }

    // TODO: play music as background service

    private fun openPlaylistFragment() {
        if (supportFragmentManager.findFragmentByTag(PlaylistFragment.TAG).isHidden) {
            supportFragmentManager.beginTransaction()
                    .hide(homeFragment).show(playlistFragment)
                    .addToBackStack(null).commit()
        }
    }
}
