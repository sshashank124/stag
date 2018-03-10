package com.phaqlow.stag.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.phaqlow.stag.model.dao.TagDao
import com.phaqlow.stag.model.dao.SongDao
import com.phaqlow.stag.model.dao.TagSongJoinDao
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.TagSongJoin
import dagger.Module


@Module
@Database(entities = [Tag::class, Song::class, TagSongJoin::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tagDao(): TagDao
    abstract fun songDao(): SongDao
    abstract fun tagSongJoinDao(): TagSongJoinDao
}