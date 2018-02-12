package com.phaqlow.stag.util

import android.support.v7.widget.RecyclerView
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject


abstract class InteractiveRecyclerAdapter<T>(itemsList: FilterableObservableList<T>)
    : ResponsiveRecyclerAdapter<T>(itemsList) {
    private var multiSelectMode = false
    private val multiSelector = ObservableSet<T>()
    val selections get() = multiSelector.selections.toList()

    private val clicksSubject = PublishSubject.create<T>()
    val itemClicks = clicksSubject.observeOn(AndroidSchedulers.mainThread()).publish().refCount()

    private val modeChangeSubject = PublishSubject.create<Boolean>()
    val selectModeChanges = modeChangeSubject.observeOn(AndroidSchedulers.mainThread()).publish().refCount()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)

        multiSelector.changes
                .subscribe { size ->
                    val isEmpty = size == 0
                    if (isEmpty && multiSelectMode) modeChangeSubject.onNext(false)
                    else if (!isEmpty && !multiSelectMode) modeChangeSubject.onNext(true)
                    multiSelectMode = !isEmpty
                }.addTo(lifecycleDisposables)
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
        override fun bindItem(t: T) {
            super.bindItem(t)

            item?.let { item ->
                RxView.clicks(view)
                        .subscribe { itemClick(adapterPosition, item) }
                        .addTo(disposables)

                RxView.longClicks(view)
                        .subscribe { itemLongClick(adapterPosition, item) }
                        .addTo(disposables)
            }
        }
    }
}
