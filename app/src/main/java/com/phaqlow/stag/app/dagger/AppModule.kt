package com.phaqlow.stag.app.dagger

import android.arch.persistence.room.Room
import android.content.Context
import com.phaqlow.stag.app.App
import com.phaqlow.stag.persistence.dao.Songs
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(app: App): Context = app

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "stag.db").build()

    @Provides
    @Singleton
    fun provideTags(db: AppDatabase): Tags =
            Tags(db.tagDao())

    @Provides
    @Singleton
    fun provideSongs(db: AppDatabase): Songs =
            Songs(db.songDao())

    @Provides
    @Singleton
    fun provideTagSongJoins(db: AppDatabase): TagSongJoins =
            TagSongJoins(db.tagSongJoinDao())
}