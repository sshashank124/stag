package com.phaqlow.stag.ui.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.util.interfaces.ItemRemoveListener
import com.phaqlow.stag.util.ui.EditableRecyclerAdapter
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.setVisible
import kotlinx.android.synthetic.main.rv_item_song_compact.view.*


class SongsCompactRecyclerAdapter(itemsList: RxList<Song>,
                                  private val itemRemoveHandler: ItemRemoveListener<Song>)
    : EditableRecyclerAdapter<Song>(itemsList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongCompactViewHolder
            = SongCompactViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_song_compact, parent, false))

    inner class SongCompactViewHolder(v: View) : ResponsiveViewHolder(v) {
        override fun bindItem(data: Song) {
            super.bindItem(data)

            item?.let { song ->
                view.song_name.text = song.name
                view.delete_song.setVisible(canEdit)

                view.delete_song.clicks().register { itemRemoveHandler.onItemRemove(song) }
            }
        }
    }
}
