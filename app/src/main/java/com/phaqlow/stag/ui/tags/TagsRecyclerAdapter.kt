package com.phaqlow.stag.ui.tags

import android.view.View
import android.widget.ImageView
import com.phaqlow.stag.R
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.ui.SelectableRecyclerAdapter
import com.phaqlow.stag.util.ui.ResponsiveRecyclerAdapter
import kotlinx.android.synthetic.main.rv_item_tag.view.*


class TagsRecyclerAdapter(itemsList: RxList<Tag>) : SelectableRecyclerAdapter<Tag>(itemsList) {

    override val itemViewResId = R.layout.rv_item_tag

    override fun createViewHolder(itemView: View) = ViewHolder(itemView)

    override fun modifyItemView(itemView: View) {
        itemView.findViewById<ImageView>(R.id.item_icon).setImageResource(R.drawable.ic_tag)
    }

    inner class ViewHolder(v: View) : ResponsiveRecyclerAdapter<Tag>.ViewHolder(v) {
        override fun bindView(item: Tag) {
            view.item_name.text = item.name
            view.item_info.text = item.id.toString()
        }
    }
}
