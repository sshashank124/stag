package com.phaqlow.stag.persistence.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.phaqlow.stag.persistence.entity.Tag
import io.reactivex.Single


@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTag(tag: Tag): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTag(tag: Tag): Int

    @Delete
    fun deleteTag(tag: Tag): Int

    @Query("SELECT * FROM tags WHERE id = :id")
    fun getTag(id: Long): Single<Tag>

    @Query("SELECT id, name FROM tags")
    fun getAllTags(): Single<List<Tag>>
}