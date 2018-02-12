package com.phaqlow.stag.util

import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo


abstract class ResponsiveRecyclerAdapter<T>(private val itemsList: ObservableList<T>)
    : RecyclerView.Adapter<ResponsiveRecyclerAdapter<T>.ResponsiveViewHolder>() {
    internal val lifecycleDisposables = CompositeDisposable()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)

        itemsList.changes
                .filter { (op_type, _) -> op_type == ObservableList.OP_ADD }
                .subscribe { (_, position) -> notifyItemInserted(position) }
                .addTo(lifecycleDisposables)

        itemsList.changes
                .filter { (op_type, _) -> op_type == ObservableList.OP_REMOVE }
                .subscribe { (_, position) -> notifyItemRemoved(position) }
                .addTo(lifecycleDisposables)

        itemsList.changes
                .filter { (op_type, _) -> op_type == ObservableList.OP_MIXED }
                .subscribe { notifyDataSetChanged() }
                .addTo(lifecycleDisposables)
    }

    override fun getItemId(position: Int) = itemsList.data[position]!!.hashCode().toLong()
    override fun getItemCount() = itemsList.data.size

    override fun onBindViewHolder(holder: ResponsiveViewHolder, position: Int) {
        lifecycleDisposables.remove(holder.disposables)
        holder.bindItem(itemsList.data[position])
        lifecycleDisposables.add(holder.disposables)
    }

    open inner class ResponsiveViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal val view = v
        internal var item: T? = null
        internal var disposables = CompositeDisposable()

        open fun bindItem(t: T) {
            item = t
            if (disposables.isDisposed) disposables = CompositeDisposable()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        lifecycleDisposables.clear()
    }
}
