package com.phaqlow.stag.ui.tags

import android.view.View
import android.widget.ImageView
import com.phaqlow.stag.R
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.util.recycleradapters.SelectableRecyclerAdapter
import com.phaqlow.stag.util.rxcollections.RxSequence
import kotlinx.android.synthetic.main.rv_item_tag.view.*


class TagsRecyclerAdapter(items: RxSequence<Tag>) : SelectableRecyclerAdapter<Tag>(items) {

    override val itemViewResId = R.layout.rv_item_tag

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)

    override fun modifyItemView(itemView: View) =
            itemView.apply { findViewById<ImageView>(R.id.item_icon).setImageResource(R.drawable.ic_tag) }

    inner class ViewHolder(v: View) : SelectableRecyclerAdapter<Tag>.ViewHolder(v) {
        override fun bindData(item: Tag) {
            super.bindData(item)

            view.item_name.text = item.name
            view.item_info.text = item.id.toString()
        }
    }
}
