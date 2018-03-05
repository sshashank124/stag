package com.phaqlow.stag.ui.tags

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.dao.Tags
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.ui.home.ListFragment
import com.phaqlow.stag.util.setVisible
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.extra_tags_list.*
import javax.inject.Inject


// TODO: show song count for each tag
class TagsListFragment : ListFragment<Tag>() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var joins: TagSongJoins

    override fun onAttach(context: Context) {
        super.onAttach(context)
        itemsRecyclerAdapter = TagsRecyclerAdapter(itemsList)
        itemsDb = tagsDb
    }

    override fun inflateAdditionalViews(rootView: View): View {
        val searchBar = rootView.findViewById<LinearLayout>(R.id.search_bar)
        layoutInflater.inflate(R.layout.extra_tags_list, searchBar)
        rootView.findViewById<FloatingActionButton>(R.id.context_fab).setImageResource(R.drawable.ic_play)
        return rootView
    }

    override fun fabActionOnItems(selectedItems: List<Tag>) {
        // TODO: implement sending play request to MainActivity or Music Playing Service
        joins.getSongsForTags(selectedItems).register { Log.d("Stag", it.toString()) }
        itemsRecyclerAdapter.clearSelections()
    }

    override fun setRxBindings() {
        super.setRxBindings()
        Observable.merge(searchBoxChanges, itemsList.updates).register { setAddBtnVisibility() }
        add_tag_btn.clicks().withLatestFrom(searchBoxChanges).register { (_, tagName) -> addTag(tagName) }
    }

    private fun addTag(tagName: String) {
        itemsDb.insertItem(Tag(tagName)).register { itemsList.add(Tag(it, tagName, tagName)) }
    }

    private fun setAddBtnVisibility() {
        val searchText = search_input.text.trim().toString()
        val isUniqueNonEmptyName = searchText.isNotEmpty() && itemsList.fullData.none { it.name == searchText }
        add_tag_btn.setVisible(isUniqueNonEmptyName)
    }

    override val detailActivityClass = TagDetailActivity::class
}
