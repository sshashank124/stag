package com.phaqlow.stag.persistence.dao

import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.util.ioToUiThread
import io.reactivex.Completable
import io.reactivex.Single


class Songs(private val songDao: SongDao) : ItemsDb<Song> {

    override fun insertItem(item: Song) = Single.fromCallable { songDao.insertSong(item) }.ioToUiThread()

    override fun updateItem(item: Song) = Completable.fromAction { songDao.updateSong(item) }.ioToUiThread()

    override fun deleteItem(item: Song) = Completable.fromAction { songDao.deleteSong(item) }.ioToUiThread()

    override fun getItem(itemId: Long) = songDao.getSong(itemId).ioToUiThread()

    override fun getAllItems() = songDao.getAllSongs().ioToUiThread()
}