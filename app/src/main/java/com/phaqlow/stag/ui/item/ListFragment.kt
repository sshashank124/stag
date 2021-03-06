package com.phaqlow.stag.ui.item

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.ItemsDb
import com.phaqlow.stag.util.rxcollections.RxFilterableSortedVector
import com.phaqlow.stag.model.entity.Item
import com.phaqlow.stag.util.ITEM_EXTRA_ID
import com.phaqlow.stag.util.setVisible
import com.phaqlow.stag.util.toUi
import com.phaqlow.stag.util.disposables.DisposableFragment
import com.phaqlow.stag.util.recycleradapters.SelectableRecyclerAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass


abstract class ListFragment<T: Item> : DisposableFragment() {
    protected lateinit var itemsDb: ItemsDb<T>
    protected val items = RxFilterableSortedVector<T>()
    protected lateinit var itemsRecyclerAdapter: SelectableRecyclerAdapter<T>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflateAdditionalViews(inflater.inflate(R.layout.fragment_list, container, false))
    protected open fun inflateAdditionalViews(rootView: View): View = rootView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setRxBindings()
    }

    private fun initViews() {
        item_list_rv.layoutManager = LinearLayoutManager(activity)
        itemsRecyclerAdapter.setHasStableIds(true)
        item_list_rv.adapter = itemsRecyclerAdapter
    }

    override fun onResume() { super.onResume(); loadData() }
    protected open fun loadData() { itemsDb.getAllItems().register { items.setAll(it) } }

    protected lateinit var searchBoxChanges: Observable<String>
    protected open fun setRxBindings() {
        searchBoxChanges = search_input.textChanges().map { it.trim().toString() }.publish().refCount()

        searchBoxChanges.register { searchText ->
            clear_search_btn.setVisible(searchText.isNotEmpty())
            search_icon.setVisible(searchText.isEmpty())
        }

        searchBoxChanges
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .toUi()
                .register { filterItemsOnSearchText(it.toLowerCase()) }

        // clear search box
        clear_search_btn.clicks().register { search_input.text.clear() }

        // context Floating Action Button clicks
        context_fab.clicks().register { fabActionOnItems(itemsRecyclerAdapter.selections) }

        itemsRecyclerAdapter.itemClicks.register { launchItemDetailActivity(it) }
        itemsRecyclerAdapter.selectingToggles.register { context_fab.setVisible(it) }
    }
    protected abstract fun fabActionOnItems(selectedItems: List<T>)

    protected abstract val detailActivityClass: KClass<*>
    private fun launchItemDetailActivity(item: T) =
        startActivity(Intent(context, detailActivityClass.java).putExtra(ITEM_EXTRA_ID, item.id))

    private fun filterItemsOnSearchText(searchText: String) =
        items.filter { it.name.toLowerCase().contains(searchText) }
}
