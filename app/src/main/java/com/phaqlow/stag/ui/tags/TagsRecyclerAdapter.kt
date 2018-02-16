package com.phaqlow.stag.ui.tags

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.util.ui.InteractiveRecyclerAdapter
import com.phaqlow.stag.util.collections.RxList
import kotlinx.android.synthetic.main.rv_item_tag.view.*


class TagsRecyclerAdapter(itemsList: RxList<Tag>) : InteractiveRecyclerAdapter<Tag>(itemsList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder
            = TagViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_tag, parent, false))

    inner class TagViewHolder(v: View) : InteractiveViewHolder(v) {
        override fun bindItem(data: Tag) {
            super.bindItem(data)

            item?.let { tag ->
                view.tag_name.text = tag.name
//                view.tag_num_songs.text = view.context.getString(
//                        if (tag.numSongs == 1) R.string.tag_template_num_songs_one else R.string.tag_template_num_songs,
//                        tag.numSongs)
            }
        }
    }
}
