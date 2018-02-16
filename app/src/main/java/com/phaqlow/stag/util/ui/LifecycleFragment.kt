package com.phaqlow.stag.util.ui

import com.phaqlow.stag.util.interfaces.Lifecyclable
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable


abstract class LifecycleFragment : DaggerFragment(), Lifecyclable {
    override var lifecycleDisposables = CompositeDisposable()

    override fun onDestroyView() {
        super.onDestroyView()
        disposeActive()
    }
}
