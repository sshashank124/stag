package com.phaqlow.stag.util.ui

import com.phaqlow.stag.util.contracts.Lifecyclable
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable


abstract class LifecycleActivity : DaggerAppCompatActivity(), Lifecyclable {
    override var lifecycleDisposables = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        disposeActive()
    }
}
