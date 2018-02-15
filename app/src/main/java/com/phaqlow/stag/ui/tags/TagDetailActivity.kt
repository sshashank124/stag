package com.phaqlow.stag.ui.tags

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.app.App
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.ui.songs.SongsCompactRecyclerAdapter
import com.phaqlow.stag.util.collections.RxSortedList
import com.phaqlow.stag.util.setVisible
import com.r0adkll.slidr.Slidr
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

import kotlinx.android.synthetic.main.activity_tag_detail.*
import javax.inject.Inject


class TagDetailActivity : AppCompatActivity(), SongsCompactRecyclerAdapter.ItemActionListener {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var tagSongJoinsDb: TagSongJoins
    private val lifecycleDisposables = CompositeDisposable()

    private lateinit var tag: Tag
    private var songs = RxSortedList<Song>(compareBy { song -> song.name })
    private var songsRecyclerAdapter = SongsCompactRecyclerAdapter(songs, this)

    private lateinit var deleteConfirmationDialog: Dialog

    private var currentMode = MODE_VIEW

    companion object {
        const val TAG_ID_EXTRA = "TAG_ID"

        private const val MODE_VIEW = 0
        private const val MODE_EDIT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_detail)
        Slidr.attach(this)
        (application as App).appComponent.inject(this)

        initViews()
        setRxBindings()
        loadData()
    }

    private fun initViews() {
        songs_compact_list_rv.layoutManager = LinearLayoutManager(this)
        songsRecyclerAdapter.setHasStableIds(true)
        songs_compact_list_rv.adapter = songsRecyclerAdapter

        deleteConfirmationDialog = AlertDialog.Builder(this)
                .setTitle(R.string.tag_delete)
                .setMessage(R.string.tag_delete_confirmation)
                .setPositiveButton(R.string.delete) { _, _ ->
                    tagsDb.deleteTag(tag)
                            .subscribe { finish() }
                            .addTo(lifecycleDisposables)
                }.setNegativeButton(R.string.cancel, null)
                .create()
    }

    private fun switchToEditMode() {
        currentMode = MODE_EDIT
        tag_name.setVisible(false)
        tag_delete_btn.setVisible(true)
        tag_description.isEnabled = true
        edit_save_fab.setImageResource(R.drawable.ic_save)
        songsRecyclerAdapter.setEditable(true)
    }

    private fun saveAndSwitchToViewMode() {
        val newTagDescription = tag_description.text.toString()
        if (tag.description != newTagDescription || tag.numSongs != songs.size) {
            tag.description = newTagDescription
            tag.numSongs = songs.size
            tagsDb.updateTag(tag).subscribe().addTo(lifecycleDisposables)
        }
        switchToViewMode()
    }

    private fun switchToViewMode() {
        currentMode = MODE_VIEW
        tag_name.setVisible(true)
        tag_delete_btn.setVisible(false)
        tag_description.isEnabled = false
        edit_save_fab.setImageResource(R.drawable.ic_edit)
        songsRecyclerAdapter.setEditable(false)
    }

    override fun onSongRemove(song: Song) {
        tagSongJoinsDb.deleteTagSongJoin(tag.id, song.id)
                .subscribe { songs.remove(song) }
                .addTo(lifecycleDisposables)
    }

    private fun loadData() {
        intent.getLongExtra(TAG_ID_EXTRA, -1L).takeIf { it != -1L }?.let { tagId ->
            tagsDb.getTag(tagId)
                    .subscribe { t -> populateViewsWithTagData(t) }
                    .addTo(lifecycleDisposables)

            tagSongJoinsDb.getSongsForTag(Tag(tagId))
                    .subscribe { s -> songs.setAll(s) }
                    .addTo(lifecycleDisposables)
        }
    }

    private fun populateViewsWithTagData(t: Tag) {
        tag = t
        tag_name.text = tag.name
        if (tag.description != null) tag_description.setText(tag.description)
    }

    private fun setRxBindings() {
        edit_save_fab.clicks().subscribe {
            when (currentMode) {
                MODE_VIEW -> switchToEditMode()
                MODE_EDIT -> saveAndSwitchToViewMode()
            }
        }.addTo(lifecycleDisposables)

        tag_delete_btn.clicks().subscribe {
            deleteConfirmationDialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleDisposables.clear()
    }
}
