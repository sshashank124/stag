package com.phaqlow.stag.ui.tags

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.util.FilterableObservableList
import com.phaqlow.stag.util.InteractiveRecyclerAdapter
import kotlinx.android.synthetic.main.rv_item_tag.view.*


class TagsRecyclerAdapter(itemsList: FilterableObservableList<Tag>)
    : InteractiveRecyclerAdapter<Tag>(itemsList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder
            = TagViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_tag, parent, false))

    // TODO: make sorted

    inner class TagViewHolder(v: View) : InteractiveViewHolder(v) {
        override fun bindItem(t: Tag) {
            super.bindItem(t)

            item?.let { tag ->
                view.tag_name.text = tag.name
                view.tag_num_songs.text = view.context.getString(
                        if (tag.numSongs == 1) R.string.template_tag_num_songs_one else R.string.template_tag_num_songs,
                        tag.numSongs)
            }
        }
    }
}
