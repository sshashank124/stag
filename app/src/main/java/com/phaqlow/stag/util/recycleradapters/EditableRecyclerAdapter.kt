package com.phaqlow.stag.util.recycleradapters

import com.phaqlow.stag.util.rxcollections.RxSequence


abstract class EditableRecyclerAdapter<T>(items: RxSequence<T>)
    : ResponsiveRecyclerAdapter<T>(items) {
    protected var canEdit: Boolean = false

    fun setEditable(e: Boolean) {
        canEdit = e
        notifyDataSetChanged()
    }
}