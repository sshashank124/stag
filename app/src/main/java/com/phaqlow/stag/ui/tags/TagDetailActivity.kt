package com.phaqlow.stag.ui.tags

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.jakewharton.rxbinding2.view.RxView
import com.phaqlow.stag.R
import com.phaqlow.stag.app.App
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.ui.songs.SongsCompactRecyclerAdapter
import com.phaqlow.stag.util.ObservableList
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
    private var songs = ObservableList<Song>()
    private var songsRecyclerAdapter = SongsCompactRecyclerAdapter(songs, this)

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
        songs_list_rv.layoutManager = LinearLayoutManager(this)
        songsRecyclerAdapter.setHasStableIds(true)
        songs_list_rv.adapter = songsRecyclerAdapter
    }

    private fun switchToEditMode() {
        currentMode = MODE_EDIT
        tag_name.setBackgroundResource(R.color.colorAccent)
        tag_name.isEnabled = true
        tag_description.isEnabled = true
        edit_save_fab.setImageResource(R.drawable.ic_save)
        songsRecyclerAdapter.setEditable(true)
    }

    private fun saveAndSwitchToViewMode() {
        if (tag.description != tag_description.text.toString() || tag.numSongs != songs.data.size) {
            tag.description = tag_description.text.toString()
            tag.numSongs = songs.data.size
            tagsDb.updateTag(tag).subscribe().addTo(lifecycleDisposables)
        }
        switchToViewMode()
    }

    private fun switchToViewMode() {
        currentMode = MODE_VIEW
        tag_name.setBackgroundResource(R.color.gray_dark)
        tag_name.isEnabled = false
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
                    .subscribe { s ->
                        songs.setData(s)
                        Log.d("Stag", "Songs fetched: $s")
                    }
                    .addTo(lifecycleDisposables)
        }
    }

    private fun populateViewsWithTagData(t: Tag) {
        tag = t
        tag_name.text = tag.name
        if (tag.description != null) tag_description.setText(tag.description)
    }

    private fun setRxBindings() {
        RxView.clicks(edit_save_fab).subscribe {
            when (currentMode) {
                MODE_VIEW -> switchToEditMode()
                MODE_EDIT -> saveAndSwitchToViewMode()
            }
        }.addTo(lifecycleDisposables)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleDisposables.clear()
    }
}
