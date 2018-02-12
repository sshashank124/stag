package com.phaqlow.stag.ui.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.util.FilterableObservableList
import com.phaqlow.stag.util.ObservableList
import com.phaqlow.stag.util.ResponsiveRecyclerAdapter
import com.phaqlow.stag.util.setViewVisibility
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.rv_item_song_compact.view.*


class SongsCompactRecyclerAdapter(itemsList: ObservableList<Song>,
                                  private val itemActionHandler: ItemActionListener)
    : ResponsiveRecyclerAdapter<Song>(itemsList) {
    private var editable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongCompactViewHolder
            = SongCompactViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_song_compact, parent, false))

    fun setEditable(e: Boolean) {
        editable = e
        notifyDataSetChanged()
    }

    inner class SongCompactViewHolder(v: View) : ResponsiveViewHolder(v) {
        override fun bindItem(t: Song) {
            super.bindItem(t)

            item?.let { song ->
                view.song_name.text = song.name
                setViewVisibility(view.delete_song, editable)

                RxView.clicks(view.delete_song)
                        .subscribe { itemActionHandler.onSongRemove(song) }
                        .addTo(disposables)
            }
        }
    }

    interface ItemActionListener {
        fun onSongRemove(song: Song)
    }
}
