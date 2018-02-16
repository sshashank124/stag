package com.phaqlow.stag.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.phaqlow.stag.util.interfaces.Entitiable


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
) : Entitiable<Song> {
    @Ignore constructor(id: Long) : this(id, "", null, "", 0, "")
    @Ignore constructor(name: String) : this(0, name, null, "", 0, "")
    @Ignore constructor(name: String, artists: List<String>?, album: String, duration_ms: Long, uri: String)
            : this(0, name, artists, album, duration_ms, uri)

    @Ignore override fun id() = id
    @Ignore override fun name() = name

    override fun compareTo(other: Song): Int = name.compareTo(other.name)
    override fun equals(other: Any?): Boolean = other != null && hashCode() == other.hashCode()
    override fun hashCode(): Int = id.toInt()
    override fun toString() = "Song($id: $name)"

    class DataConverter {
        @TypeConverter fun fromArtistsList(artistsList: List<String>?): String? = artistsList?.joinToString("||")
        @TypeConverter fun toArtistsList(artistsStr: String?): List<String>? = artistsStr?.split("||")
    }
}
