package com.phaqlow.stag.util.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.longClicks
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.contracts.Lifecyclable
import com.phaqlow.stag.util.contracts.toUi
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject


abstract class ResponsiveRecyclerAdapter<T>(private val itemsList: RxList<T>)
    : RecyclerView.Adapter<ResponsiveRecyclerAdapter<T>.ViewHolder>(), Lifecyclable {
    override var lifecycleDisposables = CompositeDisposable()

    private val clicksSubject: PublishSubject<T> = PublishSubject.create<T>()
    val itemClicks = clicksSubject.toUi().publish().refCount()

    private val longClicksSubject: PublishSubject<T> = PublishSubject.create<T>()
    val itemLongClicks = longClicksSubject.toUi().publish().refCount()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)

        itemsList.insertions.register { notifyItemInserted(it) }
        itemsList.removals.register { notifyItemRemoved(it) }
        itemsList.changes.register { notifyDataSetChanged() }
    }

    protected abstract val itemViewResId: Int
    protected open fun modifyItemView(itemView: View) {}
    protected abstract fun createViewHolder(itemView: View): ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(itemViewResId, parent, false)
        modifyItemView(itemView)
        return createViewHolder(itemView)
    }

    override fun getItemId(position: Int) = itemsList[position]!!.hashCode().toLong()
    override fun getItemCount() = itemsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        lifecycleDisposables.remove(holder.lifecycleDisposables)
        holder.bindData(itemsList[position])
        lifecycleDisposables.add(holder.lifecycleDisposables)
    }

    protected open fun itemClick(position: Int, item: T) = clicksSubject.onNext(item)
    protected open fun itemLongClick(position: Int, item: T) = longClicksSubject.onNext(item)

    abstract inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), Lifecyclable {
        internal val view = v
        internal var data: T? = null
        override var lifecycleDisposables = CompositeDisposable()

        fun bindData(data: T) {
            this.data = data
            if (lifecycleDisposables.isDisposed) lifecycleDisposables = CompositeDisposable()

            view.clicks().register { itemClick(adapterPosition, data) }
            view.longClicks().register { itemLongClick(adapterPosition, data) }
            bindView(data)
        }

        abstract fun bindView(item: T)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposeActive()
    }
}
