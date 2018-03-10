package com.phaqlow.stag.ui.tags

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.LinearLayout
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.dao.Tags
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.player.MusicPlayerServiceConnection
import com.phaqlow.stag.ui.item.ListFragment
import com.phaqlow.stag.util.setVisible
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.synthetic.main.extra_tags_list.*
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject


// TODO: show song count for each tag
class TagsListFragment : ListFragment<Tag>() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var joins: TagSongJoins

    override fun onAttach(context: Context) {
        super.onAttach(context)
        itemsRecyclerAdapter = TagsRecyclerAdapter(items)
        itemsDb = tagsDb
    }

    override fun inflateAdditionalViews(rootView: View) =
        rootView.apply {
            layoutInflater.inflate(R.layout.extra_tags_list, findViewById<LinearLayout>(R.id.search_bar))
            findViewById<FloatingActionButton>(R.id.context_fab).setImageResource(R.drawable.ic_play)
        }

    override fun fabActionOnItems(selectedItems: List<Tag>) {
        joins.getSongsForTags(selectedItems).register { MusicPlayerServiceConnection.musicPlayer?.playSongs(it) }
        itemsRecyclerAdapter.clearSelections()
    }

    override fun setRxBindings() {
        super.setRxBindings()
        Observable.merge(searchBoxChanges, items.changes).register { setAddBtnVisibility() }
        add_tag_btn.clicks().withLatestFrom(searchBoxChanges).register { addTag(it.second) }
    }

    private fun addTag(tagName: String) {
        itemsDb.insertItem(Tag(tagName)).register { items.add(Tag(it, tagName, tagName)) }
    }

    private fun setAddBtnVisibility() {
        val isUniqueNonEmptyName = search_input.text.trim().toString()
                .run { isNotEmpty() && items.referenceList.none { it.name == this } }
        add_tag_btn.setVisible(isUniqueNonEmptyName)
    }

    override val detailActivityClass = TagDetailActivity::class
}
