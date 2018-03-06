package com.phaqlow.stag.util.ui

import android.support.v7.widget.RecyclerView
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.collections.RxSet
import com.phaqlow.stag.util.toUi
import io.reactivex.subjects.PublishSubject


abstract class SelectableRecyclerAdapter<T>(itemsList: RxList<T>)
    : ResponsiveRecyclerAdapter<T>(itemsList) {
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

    override fun onBindViewHolder(holder: ResponsiveRecyclerAdapter<T>.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.isSelected = multiSelector.selections.contains(holder.data)
    }
}
