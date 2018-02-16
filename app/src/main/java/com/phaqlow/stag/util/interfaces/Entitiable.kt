package com.phaqlow.stag.util.interfaces


interface Entitiable<in T> : Comparable<T> {
    fun id(): Long
    fun name(): String
}