package com.phaqlow.stag.ui.tags

import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.dao.Tags
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.TagSongJoin
import com.phaqlow.stag.ui.item.DetailActivity
import com.phaqlow.stag.ui.songs.SongDetailActivity
import com.phaqlow.stag.ui.songs.SongsCompactRecyclerAdapter
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.extra_tag_detail.*
import javax.inject.Inject


class TagDetailActivity : DetailActivity<Tag, Song>() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var joins: TagSongJoins

    override fun onDepsInjected() {
        subItemsRecyclerAdapter = SongsCompactRecyclerAdapter(subItems, ::onItemRemove)
        itemsDb = tagsDb
    }

    override fun initItemSpecificViews() {
        layoutInflater.inflate(R.layout.extra_tag_detail, item_additional_info)
    }

    override fun playItem() {
    }

    override fun switchToEditMode() {
        super.switchToEditMode()
        tag_description.isEnabled = true
        tag_description.requestFocus()
    }

    override fun saveChanges() {
        item = Tag(item.id, item.name, tag_description.text.toString().trim())
        tagsDb.updateItem(item).register()
    }

    override fun switchToViewMode() {
        super.switchToViewMode()
        tag_description.isEnabled = false
        tag_description.setText(item.description ?: "")
    }

    override fun populateViewsWithItemData(data: Tag) {
        super.populateViewsWithItemData(data)
        data.description?.let { tag_description.setText(it) }
    }

    override fun loadSubItemsData(itemId: Long) {
        joins.getSongsForTag(Tag(itemId)).register { subItems.setAll(it) }
    }

    private fun onItemRemove(song: Song) {
        joins.deleteTagSongJoin(TagSongJoin(item.id, song.id)).register { subItems.remove(song) }
    }

    override val subItemDetailActivityClass = SongDetailActivity::class
}
