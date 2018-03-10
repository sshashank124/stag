package com.phaqlow.stag.ui.item

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.ItemsDb
import com.phaqlow.stag.util.rxcollections.RxSortedVector
import com.phaqlow.stag.util.recycleradapters.EditableRecyclerAdapter
import com.phaqlow.stag.model.entity.Item
import com.phaqlow.stag.util.DETAIL_MODE_EDIT
import com.phaqlow.stag.util.DETAIL_MODE_VIEW
import com.phaqlow.stag.util.ITEM_EXTRA_ID
import com.phaqlow.stag.util.setVisible
import com.phaqlow.stag.util.disposables.DisposableActivity
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_detail.*
import kotlin.reflect.KClass


abstract class DetailActivity<P: Item, S: Item> : DisposableActivity() {
    protected lateinit var itemsDb: ItemsDb<P>

    protected lateinit var item: P
    protected var subItems = RxSortedVector<S>()
    protected lateinit var subItemsRecyclerAdapter: EditableRecyclerAdapter<S>

    private var currentMode = DETAIL_MODE_VIEW

    private lateinit var deleteConfirmationDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        Slidr.attach(this)
        onDepsInjected()
        initViews()
        initItemSpecificViews()
        setRxBindings()
        loadData()
    }
    protected abstract fun onDepsInjected()
    protected abstract fun initItemSpecificViews()

    private fun initViews() {
        sub_items_rv.layoutManager = LinearLayoutManager(this)
        subItemsRecyclerAdapter.setHasStableIds(true)
        sub_items_rv.adapter = subItemsRecyclerAdapter

        deleteConfirmationDialog = AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.item_delete_confirmation)
                .setPositiveButton(R.string.delete) { _, _ -> itemsDb.deleteItem(item).register { finish() } }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .create()
    }

    private fun setRxBindings() {
        play_fab.clicks().register { playItem() }

        item_edit_save_btn.clicks().register {
            when (currentMode) {
                DETAIL_MODE_VIEW -> switchToEditMode()
                DETAIL_MODE_EDIT -> saveAndSwitchToViewMode()
            }
        }

        item_delete_btn.clicks().register { deleteConfirmationDialog.show() }

        subItemsRecyclerAdapter.itemClicks.register { launchSubItemDetailActivity(it) }
    }
    protected abstract fun playItem()

    protected open fun switchToEditMode() {
        currentMode = DETAIL_MODE_EDIT
        item_delete_btn.setVisible(true)
        play_fab.setVisible(false)
        item_edit_save_btn.setImageResource(R.drawable.ic_save)
        subItemsRecyclerAdapter.setEditable(true)
    }

    private fun saveAndSwitchToViewMode() {
        saveChanges()
        switchToViewMode()
    }
    protected abstract fun saveChanges()

    protected open fun switchToViewMode() {
        currentMode = DETAIL_MODE_VIEW
        item_delete_btn.setVisible(false)
        play_fab.setVisible(true)
        item_edit_save_btn.setImageResource(R.drawable.ic_edit)
        subItemsRecyclerAdapter.setEditable(false)
    }

    private fun loadData() {
        intent.getLongExtra(ITEM_EXTRA_ID, -1L).takeIf { it != -1L }?.let { id ->
            itemsDb.getItem(id).register { populateViewsWithItemData(it) }
            loadSubItemsData(id)
        }
    }
    protected abstract fun loadSubItemsData(itemId: Long)

    protected open fun populateViewsWithItemData(data: P) {
        item = data
        item_name.text = item.name
    }

    protected abstract val subItemDetailActivityClass: KClass<*>
    private fun launchSubItemDetailActivity(subItem: S) =
            startActivity(Intent(baseContext, subItemDetailActivityClass.java)
                    .putExtra(ITEM_EXTRA_ID, subItem.id))
}
