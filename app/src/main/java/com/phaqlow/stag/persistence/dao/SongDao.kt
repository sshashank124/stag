package com.phaqlow.stag.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.phaqlow.stag.persistence.entity.Song
import io.reactivex.Single


@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(song: Song): Long

    @Delete
    fun deleteSong(tag: Song): Int

    @Query("SELECT * FROM songs WHERE id = :id")
    fun getSong(id: Long): Single<Song>

    @Query("SELECT * FROM songs")
    fun getAllSongs(): Single<List<Song>>
}