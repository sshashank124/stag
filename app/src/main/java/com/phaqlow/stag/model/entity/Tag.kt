package com.phaqlow.stag.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.phaqlow.stag.util.TAG_DESCRIPTION_MAX_LEN
import com.phaqlow.stag.util.TAG_NAME_MAX_LEN


@Entity(tableName = "tags", indices = [Index("name", unique = true)])
data class Tag (
    @PrimaryKey(autoGenerate = true)
    override val id: Long,
    override val name: String,
    val description: String?
) : Item() {
    @Ignore constructor(id: Long) : this(id, "", null)
    @Ignore constructor(name: String) : this(name, name)
    @Ignore constructor(name: String, description: String)
            : this(0, name.take(TAG_NAME_MAX_LEN), description.take(TAG_DESCRIPTION_MAX_LEN))
}
