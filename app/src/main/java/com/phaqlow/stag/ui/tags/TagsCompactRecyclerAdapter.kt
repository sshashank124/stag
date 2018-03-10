package com.phaqlow.stag.ui.tags

import com.phaqlow.stag.R
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.ui.item.CompactRecyclerAdapter
import com.phaqlow.stag.util.rxcollections.RxSequence


class TagsCompactRecyclerAdapter(items: RxSequence<Tag>, onItemRemove: (Tag) -> Unit)
    : CompactRecyclerAdapter<Tag>(items, onItemRemove) {

    override val itemIconResId = R.drawable.ic_tag
}
