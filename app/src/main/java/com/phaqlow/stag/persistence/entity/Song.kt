package com.phaqlow.stag.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters


@Entity(tableName = "songs")
@TypeConverters(Song.DataConverter::class)
data class Song(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    var artists: List<String>?,
    var album: String,
    var duration_ms: Long,
    var uri: String?
) {
    @Ignore
    constructor(id: Long) : this(id, "", null, "", 0, "")

    @Ignore
    constructor(name: String, artists: List<String>?, album: String, duration_ms: Long, uri: String)
            : this(0, name, artists, album, duration_ms, uri)

    @Ignore
    constructor(name: String) : this(0, name, null, "", 0, "")

    override fun equals(other: Any?): Boolean =
            this === other || other?.javaClass == javaClass && id == (other as Song?)?.id

    override fun hashCode(): Int = id.toInt()

    override fun toString() = "Song($id: $name)"

    class DataConverter {
        @TypeConverter
        fun fromArtistsList(artistsList: List<String>?): String? = artistsList?.joinToString("||")

        @TypeConverter
        fun toArtistsList(artistsStr: String?): List<String>? = artistsStr?.split("||")
    }
}
