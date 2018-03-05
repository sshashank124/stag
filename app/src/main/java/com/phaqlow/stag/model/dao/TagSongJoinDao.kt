package com.phaqlow.stag.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.TagSongJoin
import io.reactivex.Single


@Dao
interface TagSongJoinDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTagSongJoin(tagSongJoin: TagSongJoin)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTagSongJoins(vararg tagSongJoins: TagSongJoin)

    @Delete
    fun deleteTagSongJoin(join: TagSongJoin)

    @Query("SELECT s.* FROM songs s INNER JOIN tag_song_joins j " +
            "ON s.id=j.songId WHERE j.tagId=:tagId")
    fun getSongsForTag(tagId: Long): Single<List<Song>>

    @Query("SELECT t.* FROM tags t INNER JOIN tag_song_joins j " +
            "ON t.id=j.tagId WHERE j.songId=:songId")
    fun getTagsForSong(songId: Long): Single<List<Tag>>

    @Query("SELECT s.* FROM songs s INNER JOIN tag_song_joins j " +
            "ON s.id=j.songId WHERE j.tagId IN (:tagIds)")
    fun getSongsForTags(tagIds: List<Long>): Single<List<Song>>

    @Query("SELECT * FROM tag_song_joins")
    fun getAllJoins(): Single<List<TagSongJoin>>

    @Query("SELECT t.* FROM tags t WHERE :numSongs > " +
            "(SELECT COUNT(*) FROM tag_song_joins j WHERE t.id=j.tagId AND j.songId IN (:songIds))")
    fun getAllTagsExceptWithSongs(songIds: List<Long>, numSongs: Int): Single<List<Tag>>
}