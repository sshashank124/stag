package com.phaqlow.stag.util.disposables

import android.support.v7.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable

abstract class DisposableRecyclerAdapter<VH: RecyclerView.ViewHolder>
    : RecyclerView.Adapter<VH>(), DisposableComponent {

    final override val disposables = CompositeDisposable()

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        dispose()
    }
}
