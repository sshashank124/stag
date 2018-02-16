package com.phaqlow.stag.ui.songs

import com.phaqlow.stag.persistence.dao.Songs
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.ui.home.BaseDetailActivity
import com.phaqlow.stag.ui.tags.TagsCompactRecyclerAdapter

import javax.inject.Inject


class SongDetailActivity : BaseDetailActivity<Song, Tag>() {
    @Inject lateinit var songsDb: Songs
    @Inject lateinit var tagSongJoinsDb: TagSongJoins

    override fun onInjected() {
        subItemsRecyclerAdapter = TagsCompactRecyclerAdapter(subItemsList, this)
        itemsDb = songsDb
    }

    override fun loadSubItemsData(itemId: Long) {
        tagSongJoinsDb.getTagsForSong(Song(itemId))
                .register { tags -> subItemsList.setAll(tags) }
    }

    override fun onItemRemove(item: Tag) {
        tagSongJoinsDb.deleteTagSongJoin(item.id, this.item.id)
                .register { subItemsList.remove(item) }
    }
}
