package com.phaqlow.stag.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.model.dao.ItemsDb
import com.phaqlow.stag.util.C
import com.phaqlow.stag.util.ui.EditableRecyclerAdapter
import com.phaqlow.stag.util.collections.RxSortedList
import com.phaqlow.stag.util.contracts.Item
import com.phaqlow.stag.util.setVisible
import com.phaqlow.stag.util.ui.LifecycleActivity
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_detail.*
import kotlin.reflect.KClass


// TODO: can make it so that play button is only displayed if valid musicPlayerHandler was passed in
abstract class DetailActivity<P: Item, S: Item> : LifecycleActivity() {
    private var currentMode = C.DETAIL_MODE_VIEW

    protected lateinit var itemsDb: ItemsDb<P>

    protected lateinit var item: P
    protected var subItemsList = RxSortedList<S>()
    protected lateinit var subItemsRecyclerAdapter: EditableRecyclerAdapter<S>

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
                .setNegativeButton(R.string.cancel, null)
                .create()
    }

    private fun setRxBindings() {
        play_fab.clicks().register { playItem() }

        item_edit_save_btn.clicks().register {
            when (currentMode) {
                C.DETAIL_MODE_VIEW -> switchToEditMode()
                C.DETAIL_MODE_EDIT -> saveAndSwitchToViewMode()
            }
        }

        item_delete_btn.clicks().register { deleteConfirmationDialog.show() }

        subItemsRecyclerAdapter.itemClicks.register { launchSubItemDetailActivity(it) }
    }
    protected abstract fun playItem()

    protected open fun switchToEditMode() {
        currentMode = C.DETAIL_MODE_EDIT
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
        currentMode = C.DETAIL_MODE_VIEW
        item_delete_btn.setVisible(false)
        play_fab.setVisible(true)
        item_edit_save_btn.setImageResource(R.drawable.ic_edit)
        subItemsRecyclerAdapter.setEditable(false)
    }

    private fun loadData() {
        val itemId = intent.getLongExtra(C.EXTRA_ITEM_ID, -1L)
        if (itemId != -1L) {
            itemsDb.getItem(itemId).register { populateViewsWithItemData(it) }
            loadSubItemsData(itemId)
        }
    }
    protected abstract fun loadSubItemsData(itemId: Long)

    protected open fun populateViewsWithItemData(data: P) {
        item = data
        item_name.text = item.name()
    }

    protected abstract val subItemDetailActivityClass: KClass<*>
    private fun launchSubItemDetailActivity(subItem: S) =
            startActivity(Intent(baseContext, subItemDetailActivityClass.java)
                    .putExtra(C.EXTRA_ITEM_ID, subItem.id()))
}
