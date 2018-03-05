package com.phaqlow.stag.model.dao

import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.util.contracts.ioToUi
import io.reactivex.Completable
import io.reactivex.Single


// TODO: add error conditions to all DB accessor classes in case a DB operation fails (Single)
class Tags(private val tagDao: TagDao) : ItemsDb<Tag> {

    override fun insertItem(item: Tag) = Single.fromCallable {
        val id = tagDao.insertTag(item)
        if (id != -1L) id else tagDao.getId(item.name)
    }.ioToUi()

    override fun insertItems(vararg items: Tag) = Single.fromCallable {
        tagDao.insertTags(*items)
        tagDao.getIds(items.map { it.name })
    }.ioToUi()

    override fun insertItems(items: List<Tag>) = insertItems(*items.toTypedArray())

    override fun updateItem(item: Tag) = Completable.fromAction { tagDao.updateTag(item) }.ioToUi()

    override fun deleteItem(item: Tag) = Completable.fromAction { tagDao.deleteTag(item) }.ioToUi()

    override fun getItem(itemId: Long) = tagDao.getTag(itemId).ioToUi()

    override fun getAllItems() = tagDao.getAllTags().ioToUi()
}