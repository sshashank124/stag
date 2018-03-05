package com.phaqlow.stag.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.phaqlow.stag.model.entity.Song
import io.reactivex.Single


@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSong(song: Song): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSongs(vararg songs: Song): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateSong(song: Song)

    @Delete
    fun deleteSong(tag: Song)

    @Query("SELECT id FROM songs WHERE uri=:songUri")
    fun getId(songUri: String): Long

    @Query("SELECT id FROM songs WHERE uri IN (:songUris)")
    fun getIds(songUris: List<String>): List<Long>

    @Query("SELECT * FROM songs WHERE id=:id")
    fun getSong(id: Long): Single<Song>


    // TODO: make more efficient (get only relevant fields?)
    @Query("SELECT * FROM songs")
    fun getAllSongs(): Single<List<Song>>
}