package com.phaqlow.stag.ui.songs

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.phaqlow.stag.R
import com.phaqlow.stag.app.App
import com.phaqlow.stag.persistence.dao.Songs
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.util.collections.RxFilterableSortedList
import com.phaqlow.stag.util.setVisible
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_songs_list.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


// TODO: adding songs to tags under songs list fragment
class SongsListFragment : Fragment() {
    @Inject lateinit var songsDb: Songs

    private val songsList = RxFilterableSortedList<Song>(compareBy { song -> song.name })
    private val songsRecyclerAdapter = SongsRecyclerAdapter(songsList)
    private val lifecycleDisposables = CompositeDisposable()

    companion object {
        fun newInstance() = SongsListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_songs_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity!!.application as App).appComponent.inject(this)

        initViews()
        setRxBindings()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun initViews() {
        songs_list_rv.layoutManager = LinearLayoutManager(activity)
        songsRecyclerAdapter.setHasStableIds(true)
        songs_list_rv.adapter = songsRecyclerAdapter
    }

    private fun loadData() {
        songsDb.getAllSongs()
                .subscribe { songs -> songsList.setAll(songs) }
                .addTo(lifecycleDisposables)
    }

    private fun setRxBindings() {
        val searchBoxChanges = search_song_input.textChanges()
                .map { searchCharSeq -> searchCharSeq.trim().toString() }.publish()

        // items clearBtn and searchIcon visibility
        searchBoxChanges
                .subscribe { searchText ->
                    clear_search_btn.setVisible(searchText.isNotEmpty())
                    search_song_icon.setVisible(searchText.isEmpty())
                }.addTo(lifecycleDisposables)

        // filter songs
        searchBoxChanges
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { searchText ->
                    songsList.filter { song ->
                        song.name.toLowerCase().contains(searchText.toLowerCase())
                    }
                }.addTo(lifecycleDisposables)

        // clear search box
        clear_search_btn.clicks()
                .subscribe { clearSearchBoxText() }
                .addTo(lifecycleDisposables)

        // connect ConnectableObservables to begin emitting
        searchBoxChanges.connect().addTo(lifecycleDisposables)

        // songs recycler view item click
        songsRecyclerAdapter.itemClicks
                .subscribe { song -> showSongDetails(song) }
                .addTo(lifecycleDisposables)

        songsRecyclerAdapter.selectModeChanges
                .subscribe { inSelectMode -> add_to_tag_btn.setVisible(inSelectMode) }
                .addTo(lifecycleDisposables)

        add_to_tag_btn.clicks()
                .subscribe { addSongsToTag() }
                .addTo(lifecycleDisposables)
    }

    private fun showSongDetails(song: Song) {
        startActivity(Intent(context, SongDetailActivity::class.java)
                .putExtra(SongDetailActivity.SONG_ID_EXTRA, song.id))
    }

    private fun addSongsToTag() {
        // TODO: implement adding selected songs to a specific or multiple tags
    }

    private fun clearSearchBoxText() = search_song_input.text.clear()

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleDisposables.clear()
    }
}
