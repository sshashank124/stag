package com.phaqlow.stag.ui.item

import android.view.View
import android.widget.ImageView
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.util.recycleradapters.EditableRecyclerAdapter
import com.phaqlow.stag.util.rxcollections.RxSequence
import com.phaqlow.stag.model.entity.Item
import com.phaqlow.stag.util.setVisible
import com.phaqlow.stag.util.recycleradapters.ResponsiveRecyclerAdapter
import kotlinx.android.synthetic.main.rv_item_compact.view.*


abstract class CompactRecyclerAdapter<T: Item>(items: RxSequence<T>, private val onItemRemove: (T) -> Unit)
    : EditableRecyclerAdapter<T>(items) {

    override val itemViewResId = R.layout.rv_item_compact

    protected abstract val itemIconResId: Int
    override fun modifyItemView(itemView: View) =
            itemView.apply { findViewById<ImageView>(R.id.item_icon).setImageResource(itemIconResId) }

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)

    open inner class ViewHolder(v: View) : ResponsiveRecyclerAdapter<T>.ViewHolder(v) {
        init {
            view.delete_item.clicks().register { data?.let { onItemRemove(it) } }
        }

        override fun bindData(item: T) {
            super.bindData(item)

            view.item_name.text = item.name
            view.delete_item.setVisible(canEdit)
        }
    }
}
