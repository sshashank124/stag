package com.phaqlow.stag.util.recycleradapters

import android.support.v7.widget.RecyclerView
import android.view.View
import com.phaqlow.stag.util.rxcollections.RxSequence
import com.phaqlow.stag.util.rxcollections.RxSet
import com.phaqlow.stag.util.toUi
import io.reactivex.subjects.PublishSubject


abstract class SelectableRecyclerAdapter<T>(items: RxSequence<T>)
    : ResponsiveRecyclerAdapter<T>(items) {
    private var multiSelectMode = false
    private val multiSelector = RxSet<T>()
    val selections get() = multiSelector.selections.toList()

    private val modeChangeSubject = PublishSubject.create<Boolean>()
    val selectingToggles = modeChangeSubject.toUi().publish().refCount()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        multiSelector.updates.map { it == 0 }.register { isEmpty ->
            if (isEmpty && multiSelectMode) modeChangeSubject.onNext(false)
            else if (!isEmpty && !multiSelectMode) modeChangeSubject.onNext(true)
            multiSelectMode = !isEmpty
        }
    }

    override fun itemClick(position: Int, item: T) {
        if (multiSelectMode) toggle(position, item)
        else super.itemClick(position, item)
    }

    override fun itemLongClick(position: Int, item: T) {
        if (!multiSelectMode) toggle(position, item)
    }

    private fun toggle(position: Int, item: T) {
        multiSelector.toggle(item)
        notifyItemChanged(position)
    }

    fun clearSelections() {
        multiSelector.clear()
        notifyDataSetChanged()
    }

    open inner class ViewHolder(v: View) : ResponsiveRecyclerAdapter<T>.ViewHolder(v) {
        override fun bindData(item: T) {
            super.bindData(item)

            view.isSelected = multiSelector.selections.contains(item)
        }
    }
}
