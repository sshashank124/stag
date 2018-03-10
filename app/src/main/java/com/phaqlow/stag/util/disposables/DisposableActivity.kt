package com.phaqlow.stag.util.disposables

import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable


abstract class DisposableActivity : DaggerAppCompatActivity(), DisposableComponent {
    override val disposables = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        dispose()
    }
}
