package com.phaqlow.stag.ui.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.util.adapters.InteractiveRecyclerAdapter
import com.phaqlow.stag.util.collections.RxList
import kotlinx.android.synthetic.main.rv_item_song.view.*


class SongsRecyclerAdapter(itemsList: RxList<Song>) : InteractiveRecyclerAdapter<Song>(itemsList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder
            = SongViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_song, parent, false))

    inner class SongViewHolder(v: View) : InteractiveViewHolder(v) {
        override fun bindItem(data: Song) {
            super.bindItem(data)

            item?.let { song ->
                view.song_name.text = song.name
                view.song_artist.text = "No Artist"
            }
        }
    }
}
