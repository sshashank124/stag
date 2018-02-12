package com.phaqlow.stag.util


class FilterableObservableList<T> : ObservableList<T>() {
    private val filteredList = mutableListOf<T>()
    override val data get() = filteredList
    val baseData get() = super.data

    override fun add(value: T) {
        list.add(value)
        filteredList.add(value)
        subject.onNext(Pair(OP_ADD, filteredList.size - 1))
    }

    override fun remove(value: T) {
        filteredList.indexOf(value).takeUnless { it == -1 }?.let { pos ->
            filteredList.removeAt(pos)
            list.remove(value)
            subject.onNext(Pair(OP_REMOVE, pos))
        }
    }

    fun filter(constraint: (T) -> Boolean) {
        val newFilteredList = list.filter(constraint)
        if (filteredList != newFilteredList) {
            filteredList.clear()
            filteredList.addAll(newFilteredList)
            subject.onNext(Pair(OP_MIXED, 0))
        }
    }

    override fun setData(data: List<T>) {
        list.clear()
        list.addAll(data)
        filteredList.clear()
        filteredList.addAll(data)
        subject.onNext(Pair(OP_MIXED, 0))
    }
}
