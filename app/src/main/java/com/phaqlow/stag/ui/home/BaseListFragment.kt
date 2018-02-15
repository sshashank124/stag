package com.phaqlow.stag.ui.home

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.phaqlow.stag.R
import com.phaqlow.stag.app.App
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.ui.tags.TagDetailActivity
import com.phaqlow.stag.ui.tags.TagsRecyclerAdapter
import com.phaqlow.stag.util.collections.RxFilterableSortedList
import com.phaqlow.stag.util.setVisible
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.synthetic.main.fragment_tags_list.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


abstract class BaseListFragment<T>(sortComparator: Comparator<T>) : Fragment() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var tagSongJoinsDb: TagSongJoins

    private val itemsList = RxFilterableSortedList(sortComparator)
    private val tagsRecyclerAdapter = TagsRecyclerAdapter(itemsList)
    private val lifecycleDisposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val parentView = inflater.inflate(R.layout.fragment_tags_list, container, false)
    }

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
        tags_list_rv.layoutManager = LinearLayoutManager(activity)
        tagsRecyclerAdapter.setHasStableIds(true)
        tags_list_rv.adapter = tagsRecyclerAdapter
    }

    private fun loadData() {
        tagsDb.getAllTags()
                .subscribe { tags -> tagsList.setAll(tags) }
                .addTo(lifecycleDisposables)
    }

    private fun setRxBindings() {
        val searchBoxChanges = search_tag_input.textChanges()
                .map { searchCharSeq -> searchCharSeq.trim().toString() }.publish()

        // items clearBtn and searchIcon visibility
        searchBoxChanges
                .subscribe { searchText ->
                    clear_search_btn.setVisible(searchText.isNotEmpty())
                    search_tag_icon.setVisible(searchText.isEmpty())
                }.addTo(lifecycleDisposables)

        // items addBtn visibility
        Observable.merge(searchBoxChanges, tagsList.changes)
                .subscribe { setAddBtnVisibility() }
                .addTo(lifecycleDisposables)

        // filter tags
        searchBoxChanges
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { searchText ->
                    tagsList.filter { tag ->
                        tag.name.toLowerCase().contains(searchText.toLowerCase())
                    }
                }.addTo(lifecycleDisposables)

        // add tag
        add_tag_btn.clicks()
                .withLatestFrom(searchBoxChanges)
                .subscribe { (_, tagName) -> addTag(tagName) }
                .addTo(lifecycleDisposables)

        // clear search box
        clear_search_btn.clicks()
                .subscribe { clearSearchBoxText() }
                .addTo(lifecycleDisposables)

        // connect ConnectableObservables to begin emitting
        searchBoxChanges.connect().addTo(lifecycleDisposables)

        // tags recycler view item click
        tagsRecyclerAdapter.itemClicks
                .subscribe { tag -> showTagDetails(tag) }
                .addTo(lifecycleDisposables)

        tagsRecyclerAdapter.selectModeChanges
                .subscribe { inSelectMode -> play_selected_tags_btn.setVisible(inSelectMode) }
                .addTo(lifecycleDisposables)

        play_selected_tags_btn.clicks()
                .subscribe { sendPlayRequest() }
                .addTo(lifecycleDisposables)
    }

    private fun showTagDetails(tag: Tag) {
        startActivity(Intent(context, TagDetailActivity::class.java)
                .putExtra(TagDetailActivity.TAG_ID_EXTRA, tag.id))
    }

    private fun sendPlayRequest() {
        // TODO: implement sending play request to MainActivity
        tagSongJoinsDb.getSongsForTags(tagsRecyclerAdapter.selections)
                .subscribe { songs -> Log.d("Stag", songs.toString()) }
                .addTo(lifecycleDisposables)
        tagsRecyclerAdapter.clearSelections()
    }

    private fun addTag(tagName: String) {
        val tag = Tag(tagName)
        tagsDb.insertTag(tag)
                .subscribe { tagId ->
                    tag.id = tagId
                    tagsList.add(tag)
                }.addTo(lifecycleDisposables)
    }

    private fun setAddBtnVisibility() {
        val searchText = search_tag_input.text.trim().toString()
        val isUniqueNonEmptyName = searchText.isNotEmpty()
                && tagsList.fullData.none { tag -> tag.name == searchText }
        add_tag_btn.setVisible(isUniqueNonEmptyName)
    }

    private fun clearSearchBoxText() = search_tag_input.text.clear()

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleDisposables.clear()
    }
}
