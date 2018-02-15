package com.phaqlow.stag.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey


private const val MAX_TAG_LEN: Int = 20
private const val MAX_DESCRIPTION_LEN: Int = 200

@Entity(tableName = "tags", indices = [Index("name", unique = true)])
data class Tag(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    var description: String?,
    var numSongs: Int
) {
    @Ignore
    constructor(id: Long) : this(id, "", null, 0)

    @Ignore
    constructor(name: String) : this(name, name, 0)
    
    @Ignore
    constructor(name: String, description: String? = null, numSongs: Int)
            : this(0, name.take(MAX_TAG_LEN), description?.take(MAX_DESCRIPTION_LEN), numSongs)

    override fun equals(other: Any?): Boolean =
            this === other || other?.javaClass == javaClass && id == (other as Tag?)?.id

    override fun hashCode(): Int = id.toInt()

    override fun toString() = "Tag($id: $name)"
}
