package com.phaqlow.stag.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.SparseArray
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.app.App
import com.phaqlow.stag.persistence.dao.Songs
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.ui.home.HomeFragment
import com.phaqlow.stag.ui.playlist.PlaylistFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    private val lifecycleDisposables = CompositeDisposable()
    private val homeFragment = HomeFragment.newInstance()
    private val playlistFragment = PlaylistFragment.newInstance()

    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var tagSongJoinsDb: TagSongJoins

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as App).appComponent.inject(this)

        initViews()
        setRxBindings()
        //createTestData()
        listData()
    }

    private fun listData() {
        tagsDb.getAllTags().subscribe { tags ->
            Log.d("Stag", "All Tags: $tags")
            tagSongJoinsDb.getAllJoins().subscribe { tsjs ->
                Log.d("Stag", "All Joins: $tsjs")
            }.addTo(lifecycleDisposables)
        }.addTo(lifecycleDisposables)
        songsDb.getAllSongs().subscribe { songs ->
            Log.d("Stag", "All Songs: $songs")
        }.addTo(lifecycleDisposables)
    }

    private val tagIds = SparseArray<Long>()
    private val songIds = SparseArray<Long>()

    private fun createTestData() {
        fun createJoins() {
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(1), songIds.get(1)).subscribe().addTo(lifecycleDisposables)
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(1), songIds.get(3)).subscribe().addTo(lifecycleDisposables)
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(1), songIds.get(5)).subscribe().addTo(lifecycleDisposables)

            tagSongJoinsDb.insertTagSongJoin(tagIds.get(2), songIds.get(2)).subscribe().addTo(lifecycleDisposables)
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(2), songIds.get(4)).subscribe().addTo(lifecycleDisposables)
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(2), songIds.get(6)).subscribe().addTo(lifecycleDisposables)

            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(1)).subscribe().addTo(lifecycleDisposables)
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(2)).subscribe().addTo(lifecycleDisposables)
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(3)).subscribe().addTo(lifecycleDisposables)
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(4)).subscribe().addTo(lifecycleDisposables)
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(5)).subscribe().addTo(lifecycleDisposables)
            tagSongJoinsDb.insertTagSongJoin(tagIds.get(3), songIds.get(6)).subscribe().addTo(lifecycleDisposables)
        }

        fun tagAdded(tagId: Long, idx: Int) {
            tagIds.put(idx, tagId)
            if (tagIds.size() == 3 && songIds.size()== 6) createJoins()
        }

        fun songAdded(songId: Long, idx: Int) {
            songIds.put(idx, songId)
            if (tagIds.size() == 3 && songIds.size() == 6) createJoins()
        }

        tagsDb.insertTag(Tag("T3")).subscribe { id -> tagAdded(id, 1) }.addTo(lifecycleDisposables)
        tagsDb.insertTag(Tag("T2")).subscribe { id -> tagAdded(id, 2) }.addTo(lifecycleDisposables)
        tagsDb.insertTag(Tag("T1")).subscribe { id -> tagAdded(id, 3) }.addTo(lifecycleDisposables)

        songsDb.insertSong(Song("S1")).subscribe { id -> songAdded(id, 1) }.addTo(lifecycleDisposables)
        songsDb.insertSong(Song("S6")).subscribe { id -> songAdded(id, 2) }.addTo(lifecycleDisposables)
        songsDb.insertSong(Song("S5")).subscribe { id -> songAdded(id, 3) }.addTo(lifecycleDisposables)
        songsDb.insertSong(Song("S4")).subscribe { id -> songAdded(id, 4) }.addTo(lifecycleDisposables)
        songsDb.insertSong(Song("S2")).subscribe { id -> songAdded(id, 5) }.addTo(lifecycleDisposables)
        songsDb.insertSong(Song("S3")).subscribe { id -> songAdded(id, 6) }.addTo(lifecycleDisposables)
    }

    private fun initViews() {
        supportFragmentManager.beginTransaction()
                .add(R.id.content, homeFragment, HomeFragment.TAG)
                .add(R.id.content, playlistFragment, PlaylistFragment.TAG)
                .hide(playlistFragment)
                .commit()
    }

    private fun setRxBindings() {
        play_bar.clicks()
                .subscribe { openPlaylistFragment() }
                .addTo(lifecycleDisposables)
    }

    // TODO: play music as background service

    private fun openPlaylistFragment() {
        if (supportFragmentManager.findFragmentByTag(PlaylistFragment.TAG).isHidden) {
            supportFragmentManager.beginTransaction()
                    .hide(homeFragment).show(playlistFragment)
                    .addToBackStack(null).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleDisposables.clear()
    }
}
