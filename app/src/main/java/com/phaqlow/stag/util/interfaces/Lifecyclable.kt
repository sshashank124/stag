package com.phaqlow.stag.util.interfaces

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo


interface Lifecyclable {
    var lifecycleDisposables: CompositeDisposable

    fun disposeActive() {
        lifecycleDisposables.clear()
    }

    fun <T> Observable<T>.register(callback: (T) -> Unit) =
            this.subscribe(callback).addTo(lifecycleDisposables)

    fun <T> Single<T>.register(callback: (T) -> Unit) =
            this.subscribe(callback).addTo(lifecycleDisposables)

    fun Completable.register(callback: () -> Unit) =
            this.subscribe(callback).addTo(lifecycleDisposables)

    fun Completable.register() =
            this.subscribe().addTo(lifecycleDisposables)
}
