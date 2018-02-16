package com.phaqlow.stag.ui.tags

import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.ui.home.BaseDetailActivity
import com.phaqlow.stag.ui.songs.SongsCompactRecyclerAdapter

import javax.inject.Inject


class TagDetailActivity : BaseDetailActivity<Tag, Song>() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var tagSongJoinsDb: TagSongJoins

    override fun onInjected() {
        subItemsRecyclerAdapter = SongsCompactRecyclerAdapter(subItemsList, this)
        itemsDb = tagsDb
    }

    override fun loadSubItemsData(itemId: Long) {
        tagSongJoinsDb.getSongsForTag(Tag(itemId))
                .register { songs -> subItemsList.setAll(songs) }
    }

    override fun onItemRemove(item: Song) {
        tagSongJoinsDb.deleteTagSongJoin(this.item.id, item.id)
                .register { subItemsList.remove(item) }
    }
}
