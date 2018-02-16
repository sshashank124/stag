package com.phaqlow.stag.ui.songs

import android.content.Context
import android.content.Intent
import com.phaqlow.stag.persistence.dao.Songs
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.ui.home.BaseDetailActivity
import com.phaqlow.stag.ui.home.BaseListFragment
import javax.inject.Inject


class SongsListFragment : BaseListFragment<Song>() {
    @Inject lateinit var songsDb: Songs

    override fun onAttach(context: Context) {
        super.onAttach(context)
        itemsRecyclerAdapter = SongsRecyclerAdapter(itemsList)
        itemsDb = songsDb
    }

    override fun filterItemsOnSearchText(searchText: String) {
        val searchTextLowerCase = searchText.toLowerCase()
        itemsList.filter { song -> song.name.toLowerCase().contains(searchTextLowerCase) }
    }

    override fun launchItemDetailsActivity(item: Song) {
        startActivity(Intent(context, SongDetailActivity::class.java)
                .putExtra(BaseDetailActivity.ITEM_ID_EXTRA, item.id))
    }

    override fun onFabClicked() {
        // TODO: add multiple selected songs to possibly multiple tags
    }
}
