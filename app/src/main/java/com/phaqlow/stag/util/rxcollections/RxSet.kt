package com.phaqlow.stag.util.rxcollections

import com.phaqlow.stag.util.toUi
import io.reactivex.subjects.PublishSubject


class RxSet<T> {
    val selections = hashSetOf<T>()

    private val subject = PublishSubject.create<Int>()
    val updates = subject.toUi().publish().refCount()

    fun toggle(value: T) {
        if (!selections.add(value)) selections.remove(value)
        subject.onNext(selections.size)
    }

    fun clear() {
        selections.clear()
        subject.onNext(selections.size)
    }
}
