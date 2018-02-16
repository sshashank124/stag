package com.phaqlow.stag.util.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import com.phaqlow.stag.util.collections.RxList
import com.phaqlow.stag.util.interfaces.Lifecyclable
import io.reactivex.disposables.CompositeDisposable


abstract class ResponsiveRecyclerAdapter<T>(private val itemsList: RxList<T>)
    : RecyclerView.Adapter<ResponsiveRecyclerAdapter<T>.ResponsiveViewHolder>(), Lifecyclable {
    override var lifecycleDisposables = CompositeDisposable()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)

        itemsList.changes
                .filter { (op_type, _) -> op_type == RxList.OP_ADD }
                .register { (_, position) -> notifyItemInserted(position) }

        itemsList.changes
                .filter { (op_type, _) -> op_type == RxList.OP_REMOVE }
                .register { (_, position) -> notifyItemRemoved(position) }

        itemsList.changes
                .filter { (op_type, _) -> op_type == RxList.OP_SET }
                .register { notifyDataSetChanged() }
    }

    override fun getItemId(position: Int) = itemsList[position]!!.hashCode().toLong()
    override fun getItemCount() = itemsList.size

    override fun onBindViewHolder(holder: ResponsiveViewHolder, position: Int) {
        lifecycleDisposables.remove(holder.lifecycleDisposables)
        holder.bindItem(itemsList[position])
        lifecycleDisposables.add(holder.lifecycleDisposables)
    }

    open inner class ResponsiveViewHolder(v: View) : RecyclerView.ViewHolder(v), Lifecyclable {
        internal val view = v
        internal var item: T? = null
        override var lifecycleDisposables = CompositeDisposable()

        open fun bindItem(data: T) {
            item = data
            if (lifecycleDisposables.isDisposed) lifecycleDisposables = CompositeDisposable()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposeActive()
    }
}
