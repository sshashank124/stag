@file:Suppress("unused")

package com.phaqlow.stag.util

import android.content.Context
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.Subject


fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun String.orIfBlank(alternative: String) = if (isBlank()) alternative else this

fun <T, U, V> List<T>.productWith(other: List<U>, combiner: (T, U) -> V): List<V> =
        this.flatMap { t -> other.map { u -> combiner(t, u) } }

// Changing Schedulers
fun <T> Subject<T>.onUi(): Observable<T> = observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.toUi(): Observable<T> = subscribeOn(AndroidSchedulers.mainThread())
fun <T> Single<T>.ioToUi(): Single<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
fun Completable.ioToUi(): Completable = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

// Shared Preferences
private fun Context.prefs() = PreferenceManager.getDefaultSharedPreferences(this)
fun Context.hasFlag(flag: String) = prefs().contains(flag)
fun Context.setFlag(flag: String) = prefs().edit().putBoolean(flag, true).apply()

// Toast and Snackbar
fun Context.longToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
fun View.shortSnackbar(msg: String) = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()

// Log
fun log(msg: String) = Log.d(C.LOG_TAG, msg)
