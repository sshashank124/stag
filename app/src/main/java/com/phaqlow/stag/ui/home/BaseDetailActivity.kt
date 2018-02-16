package com.phaqlow.stag.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.phaqlow.stag.R
import com.phaqlow.stag.persistence.dao.ItemsDb
import com.phaqlow.stag.util.interfaces.Entitiable
import com.phaqlow.stag.util.interfaces.ItemRemoveListener
import com.phaqlow.stag.util.ui.EditableRecyclerAdapter
import com.phaqlow.stag.util.collections.RxSortedList
import com.phaqlow.stag.util.setVisible
import com.phaqlow.stag.util.ui.LifecycleActivity
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_detail.*


abstract class BaseDetailActivity<P: Entitiable<P>, S: Entitiable<S>>
    : LifecycleActivity(), ItemRemoveListener<S> {
    companion object {
        const val ITEM_ID_EXTRA = "ITEM_ID"
        private const val MODE_VIEW = 0
        private const val MODE_EDIT = 1
    }
    private var currentMode = MODE_VIEW

    protected lateinit var itemsDb: ItemsDb<P>

    protected lateinit var item: P
    protected var subItemsList = RxSortedList<S>()
    protected lateinit var subItemsRecyclerAdapter: EditableRecyclerAdapter<S>

    private lateinit var deleteConfirmationDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        Slidr.attach(this)
        onInjected()

        initViews()
        setRxBindings()
        loadData()
    }

    protected abstract fun onInjected()

    private fun initViews() {
        sub_items_rv.layoutManager = LinearLayoutManager(this)
        subItemsRecyclerAdapter.setHasStableIds(true)
        sub_items_rv.adapter = subItemsRecyclerAdapter

        deleteConfirmationDialog = AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.item_delete_confirmation)
                .setPositiveButton(R.string.delete) { _, _ ->
                    itemsDb.deleteItem(item).register { finish() }
                }.setNegativeButton(R.string.cancel, null)
                .create()
    }

    private fun switchToEditMode() {
        currentMode = MODE_EDIT
        item_name.setVisible(false)
        item_delete_btn.setVisible(true)
//        tag_description.isEnabled = true
        edit_save_fab.setImageResource(R.drawable.ic_save)
        subItemsRecyclerAdapter.setEditable(true)
    }

    private fun saveAndSwitchToViewMode() {
        // save any changes
        switchToViewMode()
    }

    private fun switchToViewMode() {
        currentMode = MODE_VIEW
        item_name.setVisible(true)
        item_delete_btn.setVisible(false)
//        tag_description.isEnabled = false
        edit_save_fab.setImageResource(R.drawable.ic_edit)
        subItemsRecyclerAdapter.setEditable(false)
    }

    private fun loadData() {
        intent.getLongExtra(ITEM_ID_EXTRA, -1L).takeIf { it != -1L }?.let { itemId ->
            itemsDb.getItem(itemId).register { item -> populateViewsWithItemData(item) }
            loadSubItemsData(itemId)
        }
    }

    protected abstract fun loadSubItemsData(itemId: Long)

    private fun populateViewsWithItemData(it: P) {
        item = it
        item_name.text = item.name()
//        if (tag.description != null) tag_description.setText(tag.description)
    }

    private fun setRxBindings() {
        edit_save_fab.clicks().register {
            when (currentMode) {
                MODE_VIEW -> switchToEditMode()
                MODE_EDIT -> saveAndSwitchToViewMode()
            }
        }

        item_delete_btn.clicks().register {
            deleteConfirmationDialog.show()
        }
    }
}
