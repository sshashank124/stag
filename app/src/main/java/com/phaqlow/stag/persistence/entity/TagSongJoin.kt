package com.phaqlow.stag.persistence.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "tag_song_joins",
        indices = [Index("tagId"), Index("songId")],
        foreignKeys = [
            ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tagId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.RESTRICT),
            ForeignKey(entity = Song::class, parentColumns = ["id"], childColumns = ["songId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.RESTRICT)])
data class TagSongJoin(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val tagId: Long,
    val songId: Long
)
