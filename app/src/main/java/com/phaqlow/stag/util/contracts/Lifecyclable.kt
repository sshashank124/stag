package com.phaqlow.stag.util.contracts

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

    // subscribing and adding to disposables
    fun <T> Observable<T>.register(onNext: (T) -> Unit) = this.register(onNext, {})
    fun <T> Observable<T>.register(onNext: (T) -> Unit, onError: (Throwable) -> Unit) =
            this.subscribe(onNext, onError).addTo(lifecycleDisposables)

    fun <T> Single<T>.register(onSuccess: (T) -> Unit) = this.register(onSuccess, {})
    fun <T> Single<T>.register(onSuccess: (T) -> Unit, onError: (Throwable) -> Unit) =
            this.subscribe(onSuccess, onError).addTo(lifecycleDisposables)

    fun Completable.register() = this.register({})
    fun Completable.register(onComplete: () -> Unit) = this.register(onComplete, {})
    fun Completable.register(onComplete: () -> Unit, onError: (Throwable) -> Unit) =
            this.subscribe(onComplete, onError).addTo(lifecycleDisposables)
}
