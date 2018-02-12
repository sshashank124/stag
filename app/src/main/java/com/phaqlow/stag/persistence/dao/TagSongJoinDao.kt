package com.phaqlow.stag.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.persistence.entity.TagSongJoin
import io.reactivex.Single


@Dao
interface TagSongJoinDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTagSongJoin(tagSongJoin: TagSongJoin): Long

    @Query("DELETE FROM tag_song_joins WHERE (tagId = :tagId) AND (songId = :songId)")
    fun deleteTagSongJoin(tagId: Long, songId: Long): Int

    @Query("SELECT DISTINCT songs.* FROM songs INNER JOIN tag_song_joins ON songs.id=tag_song_joins.songId WHERE tag_song_joins.tagId LIKE :tagId")
    fun getSongsForTag(tagId: Long): Single<List<Song>>

    @Query("SELECT DISTINCT tags.* FROM tags INNER JOIN tag_song_joins ON tags.id=tag_song_joins.tagId WHERE tag_song_joins.songId LIKE :songId")
    fun getTagsForSong(songId: Long): Single<List<Tag>>

    @Query("SELECT DISTINCT songs.* FROM songs INNER JOIN tag_song_joins ON songs.id=tag_song_joins.songId WHERE tag_song_joins.tagId IN (:tagIds)")
    fun getSongsForTags(tagIds: List<Long>): Single<List<Song>>

    @Query("SELECT * FROM tag_song_joins")
    fun getAllJoins(): Single<List<TagSongJoin>>
}