package com.phaqlow.stag.persistence.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.phaqlow.stag.persistence.dao.TagDao
import com.phaqlow.stag.persistence.dao.SongDao
import com.phaqlow.stag.persistence.dao.TagSongJoinDao
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.TagSongJoin
import dagger.Module


@Module
@Database(entities = [Tag::class, Song::class, TagSongJoin::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tagDao(): TagDao
    abstract fun songDao(): SongDao
    abstract fun tagSongJoinDao(): TagSongJoinDao
}