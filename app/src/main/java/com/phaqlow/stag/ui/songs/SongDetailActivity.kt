package com.phaqlow.stag.ui.songs

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.app.App
import com.phaqlow.stag.persistence.dao.Songs
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.ui.tags.TagsCompactRecyclerAdapter
import com.phaqlow.stag.util.collections.RxSortedList
import com.phaqlow.stag.util.setVisible
import com.r0adkll.slidr.Slidr
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_song_detail.*

import javax.inject.Inject


class SongDetailActivity : AppCompatActivity(), TagsCompactRecyclerAdapter.ItemActionListener {
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var tagSongJoinsDb: TagSongJoins
    private val lifecycleDisposables = CompositeDisposable()

    private lateinit var song: Song
    private var tags = RxSortedList<Tag>(compareBy { tag -> tag.name })
    private var tagsRecyclerAdapter = TagsCompactRecyclerAdapter(tags, this)

    private lateinit var deleteConfirmationDialog: Dialog

    private var currentMode = MODE_VIEW

    companion object {
        const val SONG_ID_EXTRA = "SONG_ID"

        private const val MODE_VIEW = 0
        private const val MODE_EDIT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_detail)
        Slidr.attach(this)
        (application as App).appComponent.inject(this)

        initViews()
        setRxBindings()
        loadData()
    }

    private fun initViews() {
        tags_compact_list_rv.layoutManager = LinearLayoutManager(this)
        tagsRecyclerAdapter.setHasStableIds(true)
        tags_compact_list_rv.adapter = tagsRecyclerAdapter

        deleteConfirmationDialog = AlertDialog.Builder(this)
                .setTitle(R.string.song_delete)
                .setMessage(R.string.song_delete_confirmation)
                .setPositiveButton(R.string.delete) { _, _ ->
                    songsDb.deleteSong(song)
                            .subscribe { finish() }
                            .addTo(lifecycleDisposables)
                }.setNegativeButton(R.string.cancel, null)
                .create()
    }

    private fun switchToEditMode() {
        currentMode = MODE_EDIT
        song_name.setVisible(false)
        song_delete_btn.setVisible(true)
        edit_save_fab.setImageResource(R.drawable.ic_save)
        tagsRecyclerAdapter.setEditable(true)
    }

    private fun saveAndSwitchToViewMode() {
        // TODO: save anything that was updated
        switchToViewMode()
    }

    private fun switchToViewMode() {
        currentMode = MODE_VIEW
        song_name.setVisible(true)
        song_delete_btn.setVisible(false)
        edit_save_fab.setImageResource(R.drawable.ic_edit)
        tagsRecyclerAdapter.setEditable(false)
    }

    override fun onTagRemove(tag: Tag) {
        tagSongJoinsDb.deleteTagSongJoin(tag.id, song.id)
                .subscribe { tags.remove(tag) }
                .addTo(lifecycleDisposables)
    }

    private fun loadData() {
        intent.getLongExtra(SONG_ID_EXTRA, -1L).takeIf { it != -1L }?.let { songId ->
            songsDb.getSong(songId)
                    .subscribe { s -> populateViewsWithSongData(s) }
                    .addTo(lifecycleDisposables)

            tagSongJoinsDb.getTagsForSong(Song(songId))
                    .subscribe { t -> tags.setAll(t) }
                    .addTo(lifecycleDisposables)
        }
    }

    private fun populateViewsWithSongData(s: Song) {
        song = s
        song_name.text = song.name
    }

    private fun setRxBindings() {
        edit_save_fab.clicks().subscribe {
            when (currentMode) {
                MODE_VIEW -> switchToEditMode()
                MODE_EDIT -> saveAndSwitchToViewMode()
            }
        }.addTo(lifecycleDisposables)

        song_delete_btn.clicks().subscribe {
            deleteConfirmationDialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleDisposables.clear()
    }
}
