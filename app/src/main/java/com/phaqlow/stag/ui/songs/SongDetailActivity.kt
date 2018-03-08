package com.phaqlow.stag.ui.songs

import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.Songs
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.TagSongJoin
import com.phaqlow.stag.ui.home.DetailActivity
import com.phaqlow.stag.ui.tags.TagDetailActivity
import com.phaqlow.stag.ui.tags.TagsCompactRecyclerAdapter
import com.phaqlow.stag.util.orIfBlank
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.extra_song_detail.*
import javax.inject.Inject


class SongDetailActivity : DetailActivity<Song, Tag>() {
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var joins: TagSongJoins

    override fun onDepsInjected() {
        subItemsRecyclerAdapter = TagsCompactRecyclerAdapter(subItemsList, ::onItemRemove)
        itemsDb = songsDb
    }

    override fun initItemSpecificViews() {
        layoutInflater.inflate(R.layout.extra_song_detail, item_additional_info)
    }

    override fun playItem() {
    }

    override fun saveChanges() {}

    override fun populateViewsWithItemData(data: Song) {
        super.populateViewsWithItemData(data)
        song_artist.text = Song.joinArtistsList(data.artists, ", ").orIfBlank("N.A.")
        song_album.text = data.album.orIfBlank("N.A.")
        song_duration.text = Song.formatDuration(data.duration_ms)
    }

    override fun loadSubItemsData(itemId: Long) {
        joins.getTagsForSong(Song(itemId)).register { subItemsList.setAll(it) }
    }

    private fun onItemRemove(tag: Tag) {
        joins.deleteTagSongJoin(TagSongJoin(tag.id, item.id)).register { subItemsList.remove(tag) }
    }

    override val subItemDetailActivityClass = TagDetailActivity::class
}
