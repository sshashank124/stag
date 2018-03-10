package com.phaqlow.stag.util.disposables

import android.app.Service
import io.reactivex.disposables.CompositeDisposable


abstract class DisposableService : Service(), DisposableComponent {
    override val disposables = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        dispose()
    }
}
