package com.phaqlow.stag.ui.tags

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.phaqlow.stag.R
import com.phaqlow.stag.app.App
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.util.FilterableObservableList
import com.phaqlow.stag.util.setViewVisibility
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.synthetic.main.fragment_tags_list.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class TagsListFragment : Fragment() {
    @Inject lateinit var tagsDb: Tags
    @Inject lateinit var tagSongJoinsDb: TagSongJoins

    private val tagsList = FilterableObservableList<Tag>()
    private val tagsRecyclerAdapter = TagsRecyclerAdapter(tagsList)
    private val lifecycleDisposables = CompositeDisposable()

    companion object {
        fun newInstance() = TagsListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_tags_list, container, false)

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
                .subscribe { tags -> tagsList.setData(tags) }
                .addTo(lifecycleDisposables)
    }

    private fun setRxBindings() {
        val searchBoxChanges = RxTextView.textChanges(search_tag_input)
                .map { searchCharSeq -> searchCharSeq.trim().toString() }.publish()

        // items clearBtn and searchIcon visibility
        searchBoxChanges
                .subscribe { searchText ->
                    setViewVisibility(clear_tag_btn, searchText.isNotEmpty())
                    setViewVisibility(search_tag_icon, searchText.isEmpty())
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
        RxView.clicks(add_tag_btn)
                .withLatestFrom(searchBoxChanges)
                .subscribe { (_, tagName) -> addTag(tagName) }
                .addTo(lifecycleDisposables)

        // clear search box
        RxView.clicks(clear_tag_btn)
                .subscribe { clearSearchBoxText() }
                .addTo(lifecycleDisposables)

        // connect ConnectableObservables to begin emitting
        searchBoxChanges.connect().addTo(lifecycleDisposables)

        // tags recycler view item click
        tagsRecyclerAdapter.itemClicks
                .subscribe { tag -> launchTagDetailActivity(tag) }
                .addTo(lifecycleDisposables)

        tagsRecyclerAdapter.selectModeChanges
                .subscribe { selectMode -> setViewVisibility(play_selected_tags_btn, selectMode) }
                .addTo(lifecycleDisposables)

        RxView.clicks(play_selected_tags_btn)
                .subscribe { sendPlayRequest() }
                .addTo(lifecycleDisposables)
    }

    private fun launchTagDetailActivity(tag: Tag) {
        startActivity(Intent(context, TagDetailActivity::class.java)
                .putExtra(TagDetailActivity.TAG_ID_EXTRA, tag.id))
    }

    private fun sendPlayRequest() {
        // TODO: implement
        tagSongJoinsDb.getSongsForTags(tagsRecyclerAdapter.selections)
                .subscribe { songs -> Log.d("Stag", songs.toString()) }
                .addTo(lifecycleDisposables)
        tagsRecyclerAdapter.clearSelections()
    }

    private fun addTag(tagName: String) {
        tagsDb.insertTag(Tag(tagName))
                .subscribe { tag -> tagsList.add(tag) }
                .addTo(lifecycleDisposables)
    }

    private fun setAddBtnVisibility() {
        val searchText = search_tag_input.text.trim().toString()
        val isUniqueNonEmptyName = searchText.isNotEmpty()
                && tagsList.baseData.none { tag -> tag.name == searchText }
        setViewVisibility(add_tag_btn, isUniqueNonEmptyName)
    }

    private fun clearSearchBoxText() = search_tag_input.text.clear()

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleDisposables.clear()
    }
}
