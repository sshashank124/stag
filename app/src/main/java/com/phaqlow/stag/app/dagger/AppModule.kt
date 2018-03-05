package com.phaqlow.stag.app.dagger

import android.arch.persistence.room.Room
import android.content.Context
import com.phaqlow.stag.app.App
import com.phaqlow.stag.model.dao.Songs
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.dao.Tags
import com.phaqlow.stag.model.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule {
    @Provides @Singleton
    fun provideContext(app: App): Context = app

    @Provides @Singleton
    fun provideAppDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "stag.db")
                    // TODO: remove this eventually
                    .fallbackToDestructiveMigration().build()

    @Provides @Singleton
    fun provideTags(db: AppDatabase) = Tags(db.tagDao())

    @Provides @Singleton
    fun provideSongs(db: AppDatabase) = Songs(db.songDao())

    @Provides @Singleton
    fun provideTagSongJoins(db: AppDatabase) = TagSongJoins(db.tagSongJoinDao())
}