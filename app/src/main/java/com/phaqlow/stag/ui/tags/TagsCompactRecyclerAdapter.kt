package com.phaqlow.stag.ui.tags

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.adapters.ResponsiveRecyclerAdapter
import com.phaqlow.stag.util.setVisible
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.rv_item_tag_compact.view.*


class TagsCompactRecyclerAdapter(itemsList: RxList<Tag>,
                                 private val itemActionHandler: ItemActionListener)
    : ResponsiveRecyclerAdapter<Tag>(itemsList) {
    private var editable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagCompactViewHolder
            = TagCompactViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_tag_compact, parent, false))

    fun setEditable(e: Boolean) {
        editable = e
        notifyDataSetChanged()
    }

    inner class TagCompactViewHolder(v: View) : ResponsiveViewHolder(v) {
        override fun bindItem(data: Tag) {
            super.bindItem(data)

            item?.let { tag ->
                view.tag_name.text = tag.name
                view.delete_tag.setVisible(editable)

                view.delete_tag.clicks()
                        .subscribe { itemActionHandler.onTagRemove(tag) }
                        .addTo(disposables)
            }
        }
    }

    interface ItemActionListener {
        fun onTagRemove(tag: Tag)
    }
}
