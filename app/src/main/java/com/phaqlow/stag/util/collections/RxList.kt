package com.phaqlow.stag.util.collections

import com.phaqlow.stag.util.C
import com.phaqlow.stag.util.onUi
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


abstract class RxList<T> {
    protected val subject: PublishSubject<Pair<Int, Int>> = PublishSubject.create()
    val updates = subject.onUi().publish().refCount()
    val insertions: Observable<Int> = updates.filter { it.first == C.RX_OP_ADD }.map { it.second }
    val removals: Observable<Int> = updates.filter { it.first == C.RX_OP_REMOVE }.map { it.second }
    val changes: Observable<Pair<Int, Int>> = updates.filter { it.first == C.RX_OP_SETALL }

    fun add(value: T) {
        val addPos = addImpl(value)
        if (addPos != null) subject.onNext(Pair(C.RX_OP_ADD, addPos))
    }
    protected abstract fun addImpl(value: T): Int?

    fun remove(value: T) {
        val removePos = removeImpl(value)
        if (removePos != null) subject.onNext(Pair(C.RX_OP_REMOVE, removePos))
    }
    protected abstract fun removeImpl(value: T): Int?

    fun setAll(data: List<T>) {
        setAllImpl(data)
        subject.onNext(Pair(C.RX_OP_SETALL, 0))
    }
    protected abstract fun setAllImpl(data: List<T>)

    abstract operator fun get(pos: Int): T

    abstract val size: Int
}
