package com.phaqlow.stag.util.ui

import android.app.Service
import com.phaqlow.stag.util.contracts.Lifecyclable
import io.reactivex.disposables.CompositeDisposable


abstract class LifecycleService : Service(), Lifecyclable {
    override val lifecycleDisposables = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        disposeActive()
    }
}
