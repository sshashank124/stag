package com.phaqlow.stag.util.rxcollections

import com.phaqlow.stag.util.RX_OP_ADD
import com.phaqlow.stag.util.RX_OP_REMOVE
import com.phaqlow.stag.util.RX_OP_SETALL
import com.phaqlow.stag.util.onUi
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


abstract class RxSequence<T> {
    protected val subject: PublishSubject<Pair<Int, Int>> = PublishSubject.create()
    val changes = subject.onUi().publish().refCount()
    val insertions: Observable<Int> = changes.filter { it.first == RX_OP_ADD }.map { it.second }
    val removals: Observable<Int> = changes.filter { it.first == RX_OP_REMOVE }.map { it.second }
    val updates: Observable<Unit> = changes.filter { it.first == RX_OP_SETALL }.map {}

    fun add(value: T) = addImpl(value)?.let { subject.onNext(Pair(RX_OP_ADD, it)) }
    protected abstract fun addImpl(value: T): Int?

    fun remove(value: T) = removeImpl(value)?.let { subject.onNext(Pair(RX_OP_REMOVE, it)) }
    protected abstract fun removeImpl(value: T): Int?

    fun setAll(data: List<T>) {
        setAllImpl(data)
        subject.onNext(Pair(RX_OP_SETALL, 0))
    }
    protected abstract fun setAllImpl(data: List<T>)

    abstract operator fun get(index: Int): T
    abstract val size: Int
}
