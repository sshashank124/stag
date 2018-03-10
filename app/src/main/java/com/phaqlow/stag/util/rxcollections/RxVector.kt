package com.phaqlow.stag.util.rxcollections


open class RxVector<T> : RxSequence<T>() {
    protected val list = arrayListOf<T>()

    override fun addImpl(value: T): Int? {
        list.add(value)
        return list.size - 1
    }

    override fun removeImpl(value: T) = list.indexOf(value).takeIf { it >= 0 }?.also { list.removeAt(it) }

    override fun setAllImpl(data: List<T>) {
        list.clear()
        list.addAll(data)
    }

    override fun get(index: Int) = list[index]
    override val size get() = list.size
}
