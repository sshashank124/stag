package com.phaqlow.stag.util

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject


class ObservableSet<T> {
    val selections = mutableSetOf<T>()

    private val subject = PublishSubject.create<Int>()
    val changes = subject.observeOn(AndroidSchedulers.mainThread()).publish().refCount()

    fun toggle(value: T) {
        if (!selections.add(value)) selections.remove(value)
        subject.onNext(selections.size)
    }

    fun clear() {
        selections.clear()
        subject.onNext(selections.size)
    }
}
