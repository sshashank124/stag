package com.phaqlow.stag.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.phaqlow.stag.model.entity.Tag
import io.reactivex.Single


@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTag(tag: Tag): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTags(vararg items: Tag): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateTag(tag: Tag)

    @Delete
    fun deleteTag(tag: Tag)

    @Query("SELECT id FROM tags WHERE name=:tagName")
    fun getId(tagName: String): Long

    @Query("SELECT id FROM tags WHERE name IN (:tagNames)")
    fun getIds(tagNames: List<String>): List<Long>

    @Query("SELECT * FROM tags WHERE id=:id")
    fun getTag(id: Long): Single<Tag>

    @Query("SELECT id, name FROM tags")
    fun getAllTags(): Single<List<Tag>>
}