package com.phaqlow.stag.ui

import android.content.Intent
import android.os.Bundle
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.Songs
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.dao.Tags
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.TagSongJoin
import com.phaqlow.stag.util.C
import com.phaqlow.stag.util.ioToUi
import com.phaqlow.stag.util.setFlag
import com.phaqlow.stag.util.longToast
import com.phaqlow.stag.util.shortSnackbar
import com.phaqlow.stag.util.ui.LifecycleActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
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


class ImportSongsActivity : LifecycleActivity() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var joinsDb: TagSongJoins

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_songs)

        // TODO: Need to always access streaming permission
        val request = AuthenticationRequest.Builder(C.SPOTIFY_CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, C.SPOTIFY_LOGIN_REDIRECT_URI)
                .setScopes(arrayOf("playlist-read-private", "streaming"))
                .build()

        AuthenticationClient.openLoginActivity(this, C.SPOTIFY_REQUEST_CODE, request)
    }

    // TODO: de-uglify UI
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            C.SPOTIFY_REQUEST_CODE -> AuthenticationClient.getResponse(resultCode, data)
                    .takeIf { it.type == AuthenticationResponse.Type.TOKEN }
                    ?.let { loadUserDataFromSpotify(SpotifyApi().setAccessToken(it.accessToken).service) }
        }
    }

    private fun loadUserDataFromSpotify(spotifyService: SpotifyService) {
        fetchPlaylistsFromSpotify(spotifyService).register({ playlists ->
            val pendingPlaylistsCount = AtomicInteger(playlists.size)
            var successful = true
            fetch_progress.max = playlists.size
            playlists.forEach { (playlist, songs) ->
                savePlaylistToPersistence(playlist, songs).register({
                    fetch_progress.incrementProgressBy(1)
                    if (pendingPlaylistsCount.decrementAndGet() == 0) finishedLoadingData(successful)
                }, { _ ->
                    import_songs_activity.shortSnackbar("Error saving playlist: $playlist")
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
            setFlag(C.PREF_IS_LAUNCHED_BEFORE)
            notifyAndFinish("Playlists loaded successfully")
        } else {
            notifyAndFinish("Failed loading some playlists. Wizard will rerun on next launch")
        }
    }

    private fun notifyAndFinish(msg: String) {
        longToast(msg)
        finish()
    }

    override fun onDestroy() {
        Spotify.destroyPlayer(this)
        super.onDestroy()
    }
}
