package com.phaqlow.stag.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.Songs
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.dao.Tags
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.TagSongJoin
import com.phaqlow.stag.util.C
import com.phaqlow.stag.util.contracts.ioToUi
import com.phaqlow.stag.util.ui.LifecycleActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.player.Spotify
import io.reactivex.Completable
import io.reactivex.Single
import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.models.PlaylistSimple
import kotlinx.android.synthetic.main.activity_import_songs.*
import retrofit.RetrofitError
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


class ImportSongsActivity : LifecycleActivity() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var joinsDb: TagSongJoins
    private val spotifyApi = SpotifyApi()

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

    // TODO: de-uglify somehow (both code structure and UI)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != C.SPOTIFY_REQUEST_CODE) return

        val response = AuthenticationClient.getResponse(resultCode, data)
        if (response.type == AuthenticationResponse.Type.TOKEN) {
            spotifyApi.setAccessToken(response.accessToken)
            fetchPlaylists().register({ playlists ->
                val pendingPlaylists = AtomicInteger(playlists.size)
                val playlistProgresses = mutableMapOf<String, String>()
                fetch_progress.max = playlists.size
                playlists.forEach { (playlist, songs) ->
                    playlistProgresses[playlist] = "Fetching songs for: $playlist"
                    fetch_info.text = playlistProgresses.values.joinToString("\n")
                    insertPlaylist(playlist, songs).register({
                        playlistProgresses[playlist] = "Fetched songs for: $playlist"
                        fetch_info.text = playlistProgresses.values.joinToString("\n")
                        fetch_progress.incrementProgressBy(1)
                        if (pendingPlaylists.decrementAndGet() == 0) {
                            // TODO: uncomment this
//                        PreferenceManager.getDefaultSharedPreferences(baseContext).edit()
//                                .putBoolean(C.PREF_IS_LAUNCHED_BEFORE, true).apply()
                            this@ImportSongsActivity.finish()
                        }
                    }, { error ->
                        Log.d(C.LOG_TAG, "Error inserting playlist: ${Log.getStackTraceString(error)}")
                    })
                }
            }, { error ->
                Toast.makeText(baseContext, "Failed to fetch playlists", Toast.LENGTH_SHORT).show()
                Log.d(C.LOG_TAG, "Failed to fetch playlists data: ${Log.getStackTraceString(error)}")
                this@ImportSongsActivity.finish()
            })
        }
    }

    private fun fetchPlaylists() =
            Single.create<List<Pair<String, List<Song>>>> { subject ->
                try {
                    spotifyApi.service.myPlaylists.items
                            .map { Pair(it.name, fetchSongsForPlaylist(it)) }
                            .let { subject.onSuccess(it) }
                } catch (error: RetrofitError) { subject.onError(error) }
            }.ioToUi()

    private fun fetchSongsForPlaylist(playlist: PlaylistSimple) =
            spotifyApi.service.getPlaylistTracks(playlist.owner.id, playlist.id).items
                    .map { Song(it.track.name, it.track.artists.map { it.name },
                            it.track.album.name, it.track.duration_ms, it.track.uri) }

    private fun insertPlaylist(playlistName: String, songs: List<Song>) =
            Completable.create { subject ->
                try {
                    val tagId = tagsDb.insertItem(Tag(playlistName)).blockingGet()
                    val songIds = songsDb.insertItems(songs).blockingGet()
                    joinsDb.insertTagSongJoins(songIds.map { TagSongJoin(tagId, it) }).blockingAwait()
                    subject.onComplete()
                } catch (error: InterruptedException) { subject.onError(error) }
            }.ioToUi()

    override fun onDestroy() {
        Spotify.destroyPlayer(this)
        super.onDestroy()
    }
}
