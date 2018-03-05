package com.phaqlow.stag.model.dao

import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.util.contracts.ioToUi
import io.reactivex.Completable
import io.reactivex.Single


class Songs(private val songDao: SongDao) : ItemsDb<Song> {

    override fun insertItem(item: Song) = Single.fromCallable {
        val id = songDao.insertSong(item)
        if (id != -1L) id else songDao.getId(item.name)
    }.ioToUi()

    override fun insertItems(vararg items: Song) = Single.fromCallable {
        songDao.insertSongs(*items)
        songDao.getIds(items.map { it.uri })
    }.ioToUi()

    override fun insertItems(items: List<Song>) = insertItems(*items.toTypedArray())

    override fun updateItem(item: Song) = Completable.fromAction { songDao.updateSong(item) }.ioToUi()

    override fun deleteItem(item: Song) = Completable.fromAction { songDao.deleteSong(item) }.ioToUi()

    override fun getItem(itemId: Long) = songDao.getSong(itemId).ioToUi()

    override fun getAllItems() = songDao.getAllSongs().ioToUi()
}