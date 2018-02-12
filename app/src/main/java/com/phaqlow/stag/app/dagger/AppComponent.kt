package com.phaqlow.stag.app.dagger

import com.phaqlow.stag.ui.MainActivity
import com.phaqlow.stag.ui.playlist.PlaylistFragment
import com.phaqlow.stag.ui.songs.SongsListFragment
import com.phaqlow.stag.ui.tags.TagDetailActivity
import com.phaqlow.stag.ui.tags.TagsListFragment
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(target: MainActivity)

    fun inject(target: PlaylistFragment)
    fun inject(target: TagsListFragment)
    fun inject(target: SongsListFragment)

    fun inject(target: TagDetailActivity)
}