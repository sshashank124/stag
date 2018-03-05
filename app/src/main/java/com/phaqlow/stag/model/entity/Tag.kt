package com.phaqlow.stag.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.phaqlow.stag.util.C
import com.phaqlow.stag.util.contracts.Item


@Entity(tableName = "tags", indices = [Index("name", unique = true)])
data class Tag (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String?
) : Item() {
    @Ignore constructor(id: Long) : this(id, "", null)
    @Ignore constructor(name: String) : this(name, name)
    @Ignore constructor(name: String, description: String)
            : this(0, name.take(C.TAG_MAX_NAME_LEN), description.take(C.TAG_MAX_DESCRIPTION_LEN))

    @Ignore override fun id() = id
    @Ignore override fun name() = name
}
