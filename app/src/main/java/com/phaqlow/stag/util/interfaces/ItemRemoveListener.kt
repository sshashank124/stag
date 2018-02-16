package com.phaqlow.stag.util.interfaces


interface ItemRemoveListener<in T> {
    fun onItemRemove(item: T)
}