package com.phaqlow.stag.util.rxcollections


open class RxSortedVector<T : Comparable<T>> : RxVector<T>() {
    override fun addImpl(value: T): Int? = (-list.binarySearch(value) - 1).also { list.add(it, value) }

    override fun removeImpl(value: T) = list.binarySearch(value).takeIf { it >= 0 }?.also { list.removeAt(it) }

    override fun setAllImpl(data: List<T>) {
        super.setAllImpl(data)
        list.sort()
    }
}
