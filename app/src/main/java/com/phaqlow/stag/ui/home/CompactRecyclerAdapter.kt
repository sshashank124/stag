package com.phaqlow.stag.ui.home

import android.view.View
import android.widget.ImageView
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.util.ui.EditableRecyclerAdapter
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.contracts.Item
import com.phaqlow.stag.util.setVisible
import com.phaqlow.stag.util.ui.ResponsiveRecyclerAdapter
import kotlinx.android.synthetic.main.rv_item_compact.view.*


abstract class CompactRecyclerAdapter<T: Item>(itemsList: RxList<T>, private val onItemRemove: (T) -> Unit)
    : EditableRecyclerAdapter<T>(itemsList) {

    override val itemViewResId = R.layout.rv_item_compact

    protected abstract val itemIconResId: Int
    override fun modifyItemView(itemView: View) {
        itemView.findViewById<ImageView>(R.id.item_icon).setImageResource(itemIconResId)
    }

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)

    open inner class ViewHolder(v: View) : ResponsiveRecyclerAdapter<T>.ViewHolder(v) {
        override fun bindView(item: T) {
            view.item_name.text = item.name()
            view.delete_item.setVisible(canEdit)
            view.delete_item.clicks().register { onItemRemove(item) }
        }
    }
}
