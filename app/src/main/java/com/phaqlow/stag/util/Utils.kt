package com.phaqlow.stag.util

import android.view.View
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.Subject


fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun <T> Single<T>.ioToUiThread(): Single<T> =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun Completable.ioToUiThread(): Completable =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Subject<T>.onUi(): Observable<T> =
        this.observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.toUi(): Observable<T> =
        this.subscribeOn(AndroidSchedulers.mainThread())
