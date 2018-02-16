package com.phaqlow.stag.util.ui

import com.phaqlow.stag.util.interfaces.Entitiable
import com.phaqlow.stag.util.collections.RxList


abstract class EditableRecyclerAdapter<T: Entitiable<T>>(itemsList: RxList<T>)
    : ResponsiveRecyclerAdapter<T>(itemsList) {
    protected var canEdit: Boolean = false

    fun setEditable(e: Boolean) {
        canEdit = e
        notifyDataSetChanged()
    }
}