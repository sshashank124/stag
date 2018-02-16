package com.phaqlow.stag.persistence.dao

import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.util.ioToUiThread
import io.reactivex.Completable
import io.reactivex.Single


// TODO: add empty and error conditions to all DB accessor classes in case a DB operation fails (Maybe vs Single)
class Tags(private val tagDao: TagDao) : ItemsDb<Tag> {

    override fun insertItem(item: Tag) = Single.fromCallable { tagDao.insertTag(item) }.ioToUiThread()

    override fun updateItem(item: Tag) = Completable.fromAction { tagDao.updateTag(item) }.ioToUiThread()

    override fun deleteItem(item: Tag) = Completable.fromAction { tagDao.deleteTag(item) }.ioToUiThread()

    override fun getItem(itemId: Long) = tagDao.getTag(itemId).ioToUiThread()

    override fun getAllItems() = tagDao.getAllTags().ioToUiThread()
}