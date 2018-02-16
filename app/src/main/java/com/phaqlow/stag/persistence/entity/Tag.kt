package com.phaqlow.stag.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.phaqlow.stag.util.interfaces.Entitiable


@Entity(tableName = "tags",
        indices = [Index("name", unique = true)])
data class Tag (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    var description: String?
) : Entitiable<Tag> {
    @Ignore constructor(id: Long) : this(id, "", null)
    @Ignore constructor(name: String) : this(name, name)
    @Ignore constructor(name: String, description: String)
            : this(0, name.take(MAX_TAG_LEN), description.take(MAX_DESCRIPTION_LEN))

    @Ignore override fun id() = id
    @Ignore override fun name() = name

    override fun compareTo(other: Tag): Int = name.compareTo(other.name)
    override fun equals(other: Any?): Boolean = other != null && hashCode() == other.hashCode()
    override fun hashCode(): Int = id.toInt()
    override fun toString() = "Tag($id: $name)"

    companion object {
        private const val MAX_TAG_LEN: Int = 20
        private const val MAX_DESCRIPTION_LEN: Int = 200
    }
}
