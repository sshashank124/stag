package com.phaqlow.stag.ui.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.dao.ItemsDb
import com.phaqlow.stag.util.interfaces.Entitiable
import com.phaqlow.stag.util.ui.InteractiveRecyclerAdapter
import com.phaqlow.stag.util.collections.RxFilterableSortedList
import com.phaqlow.stag.util.setVisible
import com.phaqlow.stag.util.toUi
import com.phaqlow.stag.util.ui.LifecycleFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.concurrent.TimeUnit


abstract class BaseListFragment<T : Entitiable<T>> : LifecycleFragment() {
    protected lateinit var itemsDb: ItemsDb<T>
    protected val itemsList = RxFilterableSortedList<T>()
    protected lateinit var itemsRecyclerAdapter: InteractiveRecyclerAdapter<T>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)
        inflateAdditionalViews(rootView)
        return rootView
    }

    protected open fun inflateAdditionalViews(rootView: View) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setRxBindings()
    }

    protected open fun initViews() {
        item_list_rv.layoutManager = LinearLayoutManager(activity)
        itemsRecyclerAdapter.setHasStableIds(true)
        item_list_rv.adapter = itemsRecyclerAdapter
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        itemsDb.getAllItems().register { items -> itemsList.setAll(items) }
    }

    protected lateinit var searchBoxChanges: Observable<String>

    protected open fun setRxBindings() {
        searchBoxChanges = search_input.textChanges()
                .map { searchCharSeq -> searchCharSeq.trim().toString() }.publish().refCount()

        // items clearBtn and searchIcon visibility
        searchBoxChanges.register { searchText ->
            clear_search_btn.setVisible(searchText.isNotEmpty())
            search_icon.setVisible(searchText.isEmpty())
        }

        // filter tags
        searchBoxChanges
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .toUi()
                .register { searchText -> filterItemsOnSearchText(searchText) }

        // clear search box
        clear_search_btn.clicks().register { clearSearchBoxText() }

        // context Floating Action Button clicks
        context_fab.clicks().register { onFabClicked() }

        // recycler view item click
        itemsRecyclerAdapter.itemClicks
                .register { item -> launchItemDetailsActivity(item) }

        itemsRecyclerAdapter.selectModeChanges
                .register { inSelectMode -> context_fab.setVisible(inSelectMode) }
    }

    protected abstract fun filterItemsOnSearchText(searchText: String)

    protected abstract fun launchItemDetailsActivity(item: T)

    protected abstract fun onFabClicked()

    private fun clearSearchBoxText() = search_input.text.clear()
}
