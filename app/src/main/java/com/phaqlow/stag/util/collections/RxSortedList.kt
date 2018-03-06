package com.phaqlow.stag.util.collections


open class RxSortedList<T : Comparable<T>> : RxList<T>() {
    protected val list = arrayListOf<T>()
    override val size get() = list.size

    override fun addImpl(value: T): Int? {
        // convert to actual index since binary search returns inverted index
        val insertPos = -list.binarySearch(value) - 1
        list.add(insertPos, value)
        mutableListOf(1, 2, 5, 6).size
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

    override fun get(pos: Int) = list[pos]
}
