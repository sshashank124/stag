package com.phaqlow.stag.ui.songs

import com.phaqlow.stag.R
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.ui.item.CompactRecyclerAdapter
import com.phaqlow.stag.util.rxcollections.RxSequence


class SongsCompactRecyclerAdapter(items: RxSequence<Song>, onItemRemove: (Song) -> Unit)
    : CompactRecyclerAdapter<Song>(items, onItemRemove) {

    override val itemIconResId = R.drawable.ic_song
}
