package com.phaqlow.stag.app.dagger

import com.phaqlow.stag.ui.MainActivity
import com.phaqlow.stag.ui.home.HomeFragment
import com.phaqlow.stag.ui.playlist.PlaylistFragment
import com.phaqlow.stag.ui.songs.SongDetailActivity
import com.phaqlow.stag.ui.songs.SongsListFragment
import com.phaqlow.stag.ui.tags.TagDetailActivity
import com.phaqlow.stag.ui.tags.TagsListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindPlaylistFragment(): PlaylistFragment

    @ContributesAndroidInjector
    abstract fun bindHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun bindTagsListFragment(): TagsListFragment

    @ContributesAndroidInjector
    abstract fun bindSongsListFragment(): SongsListFragment

    @ContributesAndroidInjector
    abstract fun bindTagDetailActivity(): TagDetailActivity

    @ContributesAndroidInjector
    abstract fun bindSongDetailActivity(): SongDetailActivity
}
