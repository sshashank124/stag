package com.phaqlow.stag.ui.tags

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.util.interfaces.ItemRemoveListener
import com.phaqlow.stag.util.ui.EditableRecyclerAdapter
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.setVisible
import kotlinx.android.synthetic.main.rv_item_tag_compact.view.*


class TagsCompactRecyclerAdapter(itemsList: RxList<Tag>,
                                 private val itemRemoveHandler: ItemRemoveListener<Tag>)
    : EditableRecyclerAdapter<Tag>(itemsList) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagCompactViewHolder
            = TagCompactViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_tag_compact, parent, false))

    inner class TagCompactViewHolder(v: View) : ResponsiveViewHolder(v) {
        override fun bindItem(data: Tag) {
            super.bindItem(data)

            item?.let { tag ->
                view.tag_name.text = tag.name
                view.delete_tag.setVisible(canEdit)

                view.delete_tag.clicks().register { itemRemoveHandler.onItemRemove(tag) }
            }
        }
    }
}
