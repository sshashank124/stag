package com.phaqlow.stag.util.contracts


abstract class Item : Comparable<Item> {
    abstract fun id(): Long
    abstract fun name(): String

    // TODO: sort by recently added
    override fun compareTo(other: Item): Int = name().toLowerCase().compareTo(other.name().toLowerCase())
    override fun equals(other: Any?): Boolean = other != null && this.hashCode() == other.hashCode()
    override fun hashCode(): Int = id().toInt()
    override fun toString() = "${id()}: ${name()}"
}
