package com.phaqlow.stag.util.rxcollections

import com.phaqlow.stag.util.RX_OP_SETALL


class RxFilterableSortedVector<T : Comparable<T>> : RxSortedVector<T>() {
    val referenceList = arrayListOf<T>()
    private var filterConstraint: (T) -> Boolean = { true }

    override fun addImpl(value: T): Int? {
        referenceList.add(value)
        return filterConstraint(value).takeIf { true }?.let { super.addImpl(value) }
    }

    override fun removeImpl(value: T): Int? {
        referenceList.remove(value)
        return filterConstraint(value).takeIf { true }?.let { super.removeImpl(value) }
    }

    override fun setAllImpl(data: List<T>) {
        referenceList.clear()
        referenceList.addAll(data)
        filterImpl()
    }

    fun filter(constraint: (T) -> Boolean) {
        if (constraint == filterConstraint) return
        filterConstraint = constraint
        filterImpl()
        subject.onNext(Pair(RX_OP_SETALL, 0))
    }

    private fun filterImpl() = super.setAllImpl(referenceList.filter(filterConstraint))
}
