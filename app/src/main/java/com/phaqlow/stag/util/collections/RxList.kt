package com.phaqlow.stag.util.collections

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject


abstract class RxList<T> {
    protected val subject: PublishSubject<Pair<Int, Int>> = PublishSubject.create()
    val changes = subject.observeOn(AndroidSchedulers.mainThread()).publish().refCount()

    companion object {
        const val OP_ADD = 0
        const val OP_REMOVE = 1
        const val OP_SET = 2
    }

    fun add(value: T) {
        val addPos = addImpl(value)
        if (addPos != null) subject.onNext(Pair(OP_ADD, addPos))
    }

    protected abstract fun addImpl(value: T): Int?

    fun remove(value: T) {
        val removePos = removeImpl(value)
        if (removePos != null) subject.onNext(Pair(OP_REMOVE, removePos))
    }

    protected abstract fun removeImpl(value: T): Int?

    fun setAll(data: List<T>) {
        setAllImpl(data)
        subject.onNext(Pair(OP_SET, 0))
    }

    protected abstract fun setAllImpl(data: List<T>)

    abstract operator fun get(pos: Int): T

    abstract val size: Int
}
