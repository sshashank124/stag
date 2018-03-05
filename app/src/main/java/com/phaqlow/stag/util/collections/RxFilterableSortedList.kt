package com.phaqlow.stag.util.collections

import com.phaqlow.stag.util.C


class RxFilterableSortedList<T : Comparable<T>> : RxSortedList<T>() {
    private val referenceList = arrayListOf<T>()
    val fullData: List<T> get() = referenceList
    
    private var filterConstraint: (T) -> Boolean = { true }

    override fun addImpl(value: T): Int? {
        referenceList.add(value)
        if (!filterConstraint(value)) return null
        return super.addImpl(value)
    }

    override fun removeImpl(value: T): Int? {
        referenceList.remove(value)
        if (!filterConstraint(value)) return null
        return super.removeImpl(value)
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
        subject.onNext(Pair(C.RX_OP_SETALL, 0))
    }

    private fun filterImpl() = super.setAllImpl(referenceList.filter(filterConstraint))
}
