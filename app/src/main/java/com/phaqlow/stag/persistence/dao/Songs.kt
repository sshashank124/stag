package com.phaqlow.stag.persistence.dao

import com.phaqlow.stag.persistence.entity.Song
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class Songs(private val songDao: SongDao) {

    fun insertSong(song: Song): Single<Long> =
            Single.fromCallable { songDao.insertSong(song) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun deleteSong(song: Song): Completable =
            Completable.fromAction { songDao.deleteSong(song) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getSong(id: Long): Single<Song> =
            songDao.getSong(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getAllSongs(): Single<List<Song>> =
            songDao.getAllSongs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}