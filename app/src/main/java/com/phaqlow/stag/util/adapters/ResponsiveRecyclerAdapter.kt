package com.phaqlow.stag.util.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import com.phaqlow.stag.util.collections.RxList
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo


abstract class ResponsiveRecyclerAdapter<T>(private val itemsList: RxList<T>)
    : RecyclerView.Adapter<ResponsiveRecyclerAdapter<T>.ResponsiveViewHolder>() {
    internal val lifecycleDisposables = CompositeDisposable()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)

        itemsList.changes
                .filter { (op_type, _) -> op_type == RxList.OP_ADD }
                .subscribe { (_, position) -> notifyItemInserted(position) }
                .addTo(lifecycleDisposables)

        itemsList.changes
                .filter { (op_type, _) -> op_type == RxList.OP_REMOVE }
                .subscribe { (_, position) -> notifyItemRemoved(position) }
                .addTo(lifecycleDisposables)

        itemsList.changes
                .filter { (op_type, _) -> op_type == RxList.OP_SET }
                .subscribe { notifyDataSetChanged() }
                .addTo(lifecycleDisposables)
    }

    override fun getItemId(position: Int) = itemsList[position]!!.hashCode().toLong()
    override fun getItemCount() = itemsList.size

    override fun onBindViewHolder(holder: ResponsiveViewHolder, position: Int) {
        lifecycleDisposables.remove(holder.disposables)
        holder.bindItem(itemsList[position])
        lifecycleDisposables.add(holder.disposables)
    }

    open inner class ResponsiveViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal val view = v
        internal var item: T? = null
        internal var disposables = CompositeDisposable()

        open fun bindItem(data: T) {
            item = data
            if (disposables.isDisposed) disposables = CompositeDisposable()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        lifecycleDisposables.clear()
    }
}
