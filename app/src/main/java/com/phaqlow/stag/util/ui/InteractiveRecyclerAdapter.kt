package com.phaqlow.stag.util.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.longClicks
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.collections.RxSet
import com.phaqlow.stag.util.onUi
import io.reactivex.subjects.PublishSubject


abstract class InteractiveRecyclerAdapter<T>(itemsList: RxList<T>)
    : ResponsiveRecyclerAdapter<T>(itemsList) {
    private var multiSelectMode = false
    private val multiSelector = RxSet<T>()
    val selections get() = multiSelector.selections.toList()

    private val clicksSubject = PublishSubject.create<T>()
    val itemClicks = clicksSubject.onUi().publish().refCount()

    private val modeChangeSubject = PublishSubject.create<Boolean>()
    val selectModeChanges = modeChangeSubject.onUi().publish().refCount()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)

        multiSelector.changes
                .register { size ->
                    val isEmpty = size == 0
                    if (isEmpty && multiSelectMode) modeChangeSubject.onNext(false)
                    else if (!isEmpty && !multiSelectMode) modeChangeSubject.onNext(true)
                    multiSelectMode = !isEmpty
                }
    }

    private fun itemClick(position: Int, item: T) {
        if (multiSelectMode) toggle(position, item)
        else clicksSubject.onNext(item)
    }

    private fun itemLongClick(position: Int, item: T) {
        if (!multiSelectMode) toggle(position, item)
    }

    private fun toggle(position: Int, item: T) {
        multiSelector.toggle(item)
        notifyItemChanged(position)
    }

    internal fun clearSelections() {
        multiSelector.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ResponsiveViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.isSelected = multiSelector.selections.contains(holder.item)
    }

    open inner class InteractiveViewHolder(v: View) : ResponsiveViewHolder(v) {
        override fun bindItem(data: T) {
            super.bindItem(data)

            item?.let { item ->
                view.clicks().register { itemClick(adapterPosition, item) }
                view.longClicks().register { itemLongClick(adapterPosition, item) }
            }
        }
    }
}