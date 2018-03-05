package com.phaqlow.stag.model.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index


@Entity(tableName = "tag_song_joins",
        primaryKeys = ["tagId", "songId"],
        indices = [Index("tagId"), Index("songId")],
        foreignKeys = [
            ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tagId"],
                    onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
            ForeignKey(entity = Song::class, parentColumns = ["id"], childColumns = ["songId"],
                    onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)])
data class TagSongJoin(
    val tagId: Long,
    val songId: Long
)
