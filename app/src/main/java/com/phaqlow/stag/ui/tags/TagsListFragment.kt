package com.phaqlow.stag.ui.tags

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.ui.home.BaseDetailActivity
import com.phaqlow.stag.ui.home.BaseListFragment
import com.phaqlow.stag.util.setVisible
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.tags_list_additional.*
import javax.inject.Inject


class TagsListFragment : BaseListFragment<Tag>() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var tagSongJoinsDb: TagSongJoins

    override fun onAttach(context: Context) {
        super.onAttach(context)
        itemsRecyclerAdapter = TagsRecyclerAdapter(itemsList)
        itemsDb = tagsDb
    }

    override fun inflateAdditionalViews(rootView: View) {
        val searchBar = rootView.findViewById<LinearLayout>(R.id.search_bar)
        layoutInflater.inflate(R.layout.tags_list_additional, searchBar)
    }

    override fun setRxBindings() {
        super.setRxBindings()

        // items addBtn visibility
        Observable.merge(searchBoxChanges, itemsList.changes).register { setAddBtnVisibility() }

        // add tag
        add_tag_btn.clicks()
                .withLatestFrom(searchBoxChanges)
                .register { (_, tagName) -> addTag(tagName) }
    }

    override fun filterItemsOnSearchText(searchText: String) {
        val searchTextLowerCase = searchText.toLowerCase()
        itemsList.filter { tag -> tag.name.toLowerCase().contains(searchTextLowerCase) }
    }

    override fun launchItemDetailsActivity(item: Tag) {
        startActivity(Intent(context, TagDetailActivity::class.java)
                .putExtra(BaseDetailActivity.ITEM_ID_EXTRA, item.id))
    }

    override fun onFabClicked() {
        // TODO: implement sending play request to MainActivity
        tagSongJoinsDb.getSongsForTags(itemsRecyclerAdapter.selections)
                .register { songs -> Log.d("Stag", songs.toString()) }
        itemsRecyclerAdapter.clearSelections()
    }

    private fun addTag(tagName: String) {
        val tag = Tag(tagName)
        itemsDb.insertItem(tag)
                .register { tagId ->
                    tag.id = tagId
                    itemsList.add(tag)
                }
    }

    private fun setAddBtnVisibility() {
        val searchText = search_input.text.trim().toString()
        val isUniqueNonEmptyName = searchText.isNotEmpty()
                && itemsList.fullData.none { tag -> tag.name == searchText }
        add_tag_btn.setVisible(isUniqueNonEmptyName)
    }
}
