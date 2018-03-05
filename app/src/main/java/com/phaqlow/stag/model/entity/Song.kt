package com.phaqlow.stag.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.phaqlow.stag.util.contracts.Item


@Entity(tableName = "songs", indices = [(Index("uri", unique = true))])
@TypeConverters(Song.DataConverter::class)
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val artists: List<String>?,
    val album: String,
    val duration_ms: Long,
    val uri: String
) : Item() {
    @Ignore constructor(id: Long) : this(id, "", null, "", 0, "")
    @Ignore constructor(name: String) : this(name, null, "", 0, "")
    @Ignore constructor(name: String, artists: List<String>?, album: String, duration_ms: Long, uri: String)
            : this(0, name, artists, album, duration_ms, uri)

    @Ignore override fun id() = id
    @Ignore override fun name() = name

    // TODO: Sort songs by recently added (as well? (for tags))

    class DataConverter {
        @TypeConverter fun fromArtistsList(artistsList: List<String>?): String? = joinArtistsList(artistsList, "||")
        @TypeConverter fun toArtistsList(artistsStr: String?): List<String>? = artistsStr?.split("||")
    }

    companion object {
        fun joinArtistsList(artistsList: List<String>?, separator: String) = artistsList?.joinToString(separator) ?: ""
        fun formatDuration(duration_ms: Long) = (duration_ms / 1000).let { String.format("%02d:%02d", it / 60, it % 60) }
    }
}
