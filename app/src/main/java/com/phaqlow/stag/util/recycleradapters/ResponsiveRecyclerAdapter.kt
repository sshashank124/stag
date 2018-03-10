package com.phaqlow.stag.util.recycleradapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.longClicks
import com.phaqlow.stag.util.disposables.DisposableRecyclerAdapter
import com.phaqlow.stag.util.rxcollections.RxSequence
import com.phaqlow.stag.util.toUi
import io.reactivex.subjects.PublishSubject


abstract class ResponsiveRecyclerAdapter<T>(private val items: RxSequence<T>)
    : DisposableRecyclerAdapter<ResponsiveRecyclerAdapter<T>.ViewHolder>() {

    private val clicksSubject: PublishSubject<T> = PublishSubject.create<T>()
    val itemClicks = clicksSubject.toUi().publish().refCount()

    private val longClicksSubject: PublishSubject<T> = PublishSubject.create<T>()
    val itemLongClicks = longClicksSubject.toUi().publish().refCount()

    protected open fun itemClick(position: Int, item: T) = clicksSubject.onNext(item)
    protected open fun itemLongClick(position: Int, item: T) = longClicksSubject.onNext(item)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        items.insertions.register { notifyItemInserted(it) }
        items.removals.register { notifyItemRemoved(it) }
        items.updates.register { notifyDataSetChanged() }
    }

    protected abstract val itemViewResId: Int
    protected open fun modifyItemView(itemView: View) = itemView
    protected abstract fun createViewHolder(itemView: View): ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            createViewHolder(modifyItemView(LayoutInflater.from(parent.context).inflate(itemViewResId, parent, false)))

    override fun getItemId(position: Int) = items[position]!!.hashCode().toLong()
    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bindData(items[position])

    open inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view = v
        var data: T? = null

        init {
            view.clicks().register { data?.let { itemClick(adapterPosition, it) } }
            view.longClicks().register { data?.let { itemLongClick(adapterPosition, it) } }
        }

        open fun bindData(item: T) { data = item }
    }
}
