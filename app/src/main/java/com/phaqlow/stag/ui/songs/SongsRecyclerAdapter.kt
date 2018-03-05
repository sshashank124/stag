package com.phaqlow.stag.ui.songs

import android.view.View
import android.widget.ImageView
import com.phaqlow.stag.R
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.ui.SelectableRecyclerAdapter
import com.phaqlow.stag.util.ui.ResponsiveRecyclerAdapter
import kotlinx.android.synthetic.main.rv_item_song.view.*


class SongsRecyclerAdapter(itemsList: RxList<Song>) : SelectableRecyclerAdapter<Song>(itemsList) {

    override val itemViewResId = R.layout.rv_item_song

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)

    override fun modifyItemView(itemView: View) {
        itemView.findViewById<ImageView>(R.id.item_icon).setImageResource(R.drawable.ic_song)
    }

    inner class ViewHolder(v: View) : ResponsiveRecyclerAdapter<Song>.ViewHolder(v) {
        override fun bindView(item: Song) {
            view.item_name.text = item.name
            view.item_info.text = item.artists?.first() ?: "No Artist"
        }
    }
}
