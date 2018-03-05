package com.phaqlow.stag.ui.tags

import com.phaqlow.stag.R
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.ui.home.CompactRecyclerAdapter
import com.phaqlow.stag.util.collections.RxList


class TagsCompactRecyclerAdapter(itemsList: RxList<Tag>, onItemRemove: (Tag) -> Unit)
    : CompactRecyclerAdapter<Tag>(itemsList, onItemRemove) {

    override val itemIconResId = R.drawable.ic_tag
}
