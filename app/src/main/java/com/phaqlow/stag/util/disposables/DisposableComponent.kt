package com.phaqlow.stag.util.disposables

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer


interface DisposableComponent : Disposable, DisposableContainer {
    val disposables: CompositeDisposable

    override fun add(d: Disposable) = disposables.add(d)
    override fun remove(d: Disposable) = disposables.remove(d)
    override fun delete(d: Disposable) = disposables.delete(d)
    override fun isDisposed() = disposables.isDisposed
    override fun dispose() = disposables.dispose()

    // subscribing and adding to disposables
    fun <T> Observable<T>.register(onNext: (T) -> Unit) = this.register(onNext, {})
    fun <T> Observable<T>.register(onNext: (T) -> Unit, onError: (Throwable) -> Unit) =
            add(this.subscribe(onNext, onError))

    fun <T> Single<T>.register(onSuccess: (T) -> Unit) = this.register(onSuccess, {})
    fun <T> Single<T>.register(onSuccess: (T) -> Unit, onError: (Throwable) -> Unit) =
            add(this.subscribe(onSuccess, onError))

    fun Completable.register() = this.register({})
    fun Completable.register(onComplete: () -> Unit) = this.register(onComplete, {})
    fun Completable.register(onComplete: () -> Unit, onError: (Throwable) -> Unit) =
            add(this.subscribe(onComplete, onError))
}
