package com.phaqlow.stag.util.disposables

import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable


abstract class DisposableFragment : DaggerFragment(), DisposableComponent {
    override val disposables = CompositeDisposable()

    override fun onDestroyView() {
        super.onDestroyView()
        dispose()
    }
}
