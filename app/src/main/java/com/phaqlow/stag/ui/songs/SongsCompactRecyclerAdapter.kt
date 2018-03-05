package com.phaqlow.stag.ui.songs

import com.phaqlow.stag.R
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.ui.home.CompactRecyclerAdapter
import com.phaqlow.stag.util.collections.RxList


class SongsCompactRecyclerAdapter(itemsList: RxList<Song>, onItemRemove: (Song) -> Unit)
    : CompactRecyclerAdapter<Song>(itemsList, onItemRemove) {

    override val itemIconResId = R.drawable.ic_song
}
