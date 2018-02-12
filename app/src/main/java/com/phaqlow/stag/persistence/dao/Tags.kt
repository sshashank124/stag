package com.phaqlow.stag.persistence.dao

import com.phaqlow.stag.persistence.entity.Tag
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


// TODO: add empty and error conditions to this class in case a DB operation fails (Maybe vs Single)
class Tags(private val tagDao: TagDao) {

    fun insertTag(tag: Tag): Single<Tag> =
            Single.fromCallable {
                tag.id = tagDao.insertTag(tag)
                tag
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun updateTag(tag: Tag): Completable =
            Completable.fromCallable { tagDao.updateTag(tag) }
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun deleteTag(tag: Tag): Single<Tag> =
            Single.fromCallable {
                tagDao.deleteTag(tag)
                tag
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun getTag(id: Long): Single<Tag> =
            tagDao.getTag(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getAllTags(): Single<List<Tag>> =
            tagDao.getAllTags()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}