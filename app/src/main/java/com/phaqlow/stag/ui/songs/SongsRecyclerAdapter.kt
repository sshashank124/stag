package com.phaqlow.stag.ui.songs

import android.view.View
import android.widget.ImageView
import com.phaqlow.stag.R
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.util.rxcollections.RxSequence
import com.phaqlow.stag.util.recycleradapters.SelectableRecyclerAdapter
import kotlinx.android.synthetic.main.rv_item_song.view.*


class SongsRecyclerAdapter(items: RxSequence<Song>) : SelectableRecyclerAdapter<Song>(items) {

    override val itemViewResId = R.layout.rv_item_song

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)

    override fun modifyItemView(itemView: View) =
            itemView.apply { findViewById<ImageView>(R.id.item_icon).setImageResource(R.drawable.ic_song) }

    inner class ViewHolder(v: View) : SelectableRecyclerAdapter<Song>.ViewHolder(v) {
        override fun bindData(item: Song) {
            super.bindData(item)

            view.item_name.text = item.name
            view.item_info.text = item.artists?.first() ?: "No Artist"
        }
    }
}
