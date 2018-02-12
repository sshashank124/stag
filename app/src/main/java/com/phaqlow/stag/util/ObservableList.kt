package com.phaqlow.stag.util

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject


open class ObservableList<T> {
    protected val list = mutableListOf<T>()
    open val data get() = list

    internal val subject = PublishSubject.create<Pair<Int, Int>>()
    val changes = subject.observeOn(AndroidSchedulers.mainThread()).publish().refCount()

    companion object {
        const val OP_ADD = 0
        const val OP_REMOVE = 1
        const val OP_MIXED = 2
    }

    open fun add(value: T) {
        list.add(value)
        subject.onNext(Pair(OP_ADD, list.size - 1))
    }

    open fun remove(value: T) {
        list.indexOf(value).takeUnless { it == -1 }?.let { pos ->
            list.removeAt(pos)
            subject.onNext(Pair(OP_REMOVE, pos))
        }
    }

    open fun setData(data: List<T>) {
        list.clear()
        list.addAll(data)
        subject.onNext(Pair(OP_MIXED, 0))
    }
}
