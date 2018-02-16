package com.phaqlow.stag.util.collections


open class RxSortedList<T : Comparable<T>> : RxList<T>() {
    protected val list = mutableListOf<T>()

    override fun addImpl(value: T): Int? {
        // convert to actual index since binary search returns inverted index
        val insertPos = -list.binarySearch(value) - 1
        list.add(insertPos, value)
        return insertPos
    }

    override fun removeImpl(value: T): Int? {
        val removePos = list.binarySearch(value)
        if (removePos < 0) return null
        list.removeAt(removePos)
        return removePos
    }

    override fun setAllImpl(data: List<T>) {
        list.clear()
        list.addAll(data)
        list.sort()
    }

    override fun get(pos: Int): T = list[pos]

    override val size: Int get() = list.size
}
