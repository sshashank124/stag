package com.phaqlow.stag.ui

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.Songs
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.dao.Tags
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.TagSongJoin
import com.phaqlow.stag.util.PREF_IS_LAUNCHED_BEFORE
import com.phaqlow.stag.util.SPOTIFY_EXTRA_ACCESS_TOKEN
import com.phaqlow.stag.util.ioToUi
import com.phaqlow.stag.util.longToast
import com.phaqlow.stag.util.setFlag
import com.phaqlow.stag.util.shortSnackbar
import com.phaqlow.stag.util.disposables.DisposableActivity
import com.spotify.sdk.android.player.Spotify
import io.reactivex.Completable
import io.reactivex.Single
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.PlaylistSimple
import kotlinx.android.synthetic.main.activity_import_songs.*
import retrofit.RetrofitError
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


class ImportSongsActivity : DisposableActivity() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var joinsDb: TagSongJoins

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_songs)
        setLoadingAnimation(R.drawable.anim_stag_loading)

        intent.getStringExtra(SPOTIFY_EXTRA_ACCESS_TOKEN)
                ?.let { loadUserDataFromSpotify(SpotifyApi().setAccessToken(it).service) }
    }

    private fun loadUserDataFromSpotify(spotifyService: SpotifyService) {
        fetchPlaylistsFromSpotify(spotifyService).register({ playlists ->
            val pendingPlaylistsCount = AtomicInteger(playlists.size)
            var successful = true
            playlists.forEach { (playlist, songs) ->
                savePlaylistToPersistence(playlist, songs).register({
                    if (pendingPlaylistsCount.decrementAndGet() == 0) finishedLoadingData(successful)
                }, { _ ->
                    shortSnackbar("Error saving playlist: $playlist", import_songs_activity)
                    pendingPlaylistsCount.getAndDecrement()
                    successful = false
                })
            }
        }, { _ -> notifyAndFinish("Error fetching playlists") })
    }

    private fun fetchPlaylistsFromSpotify(spotifyService: SpotifyService) =
            Single.create<List<Pair<String, List<Song>>>> { subject ->
                try {
                    spotifyService.myPlaylists.items
                            .map { Pair(it.name, fetchSongsForPlaylistFromSpotify(it, spotifyService)) }
                            .let { subject.onSuccess(it) }
                } catch (error: RetrofitError) { subject.onError(error) }
            }.ioToUi()

    private fun fetchSongsForPlaylistFromSpotify(playlist: PlaylistSimple, spotifyService: SpotifyService) =
            spotifyService.getPlaylistTracks(playlist.owner.id, playlist.id).items
                    .map { Song(it.track.name, it.track.artists.map { it.name },
                            it.track.album.name, it.track.duration_ms, it.track.uri) }

    private fun savePlaylistToPersistence(playlistName: String, songs: List<Song>) =
            Completable.create { subject ->
                try {
                    val tagId = tagsDb.insertItem(Tag(playlistName)).blockingGet()
                    val songIds = songsDb.insertItems(songs).blockingGet()
                    joinsDb.insertTagSongJoins(songIds.map { TagSongJoin(tagId, it) }).blockingAwait()
                    subject.onComplete()
                } catch (error: InterruptedException) { subject.onError(error) }
            }.ioToUi()

    private fun finishedLoadingData(successful: Boolean) {
        if (successful) {
            setFlag(PREF_IS_LAUNCHED_BEFORE)
            setLoadingAnimation(R.drawable.anim_stag_complete)
            Handler().postDelayed({ notifyAndFinish("Playlists loaded successfully") },
                    resources.getInteger(R.integer.anim_stag_complete_duration).toLong())
        } else notifyAndFinish("Failed loading some playlists. Wizard will rerun on next launch")
    }

    private fun setLoadingAnimation(@DrawableRes animation: Int) {
        val animationDrawable = AnimatedVectorDrawableCompat.create(this, animation)
        loading_icon.setImageDrawable(animationDrawable)
        (animationDrawable as Animatable).start()
    }

    private fun notifyAndFinish(msg: String) {
        longToast(msg)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        longToast("User data import interrupted. Wizard will rerun on next launch")
    }
}
