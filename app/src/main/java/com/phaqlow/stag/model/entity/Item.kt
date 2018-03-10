package com.phaqlow.stag.model.entity


abstract class Item : Comparable<Item> {
    abstract val id: Long
    abstract val name: String

    // TODO: sort by recently added
    override fun compareTo(other: Item): Int = name.toLowerCase().compareTo(other.name.toLowerCase())
    override fun equals(other: Any?): Boolean = other != null && (other is Item) && this.id == other.id
    override fun hashCode(): Int = id.toInt()
    override fun toString() = "$id: $name"
}
