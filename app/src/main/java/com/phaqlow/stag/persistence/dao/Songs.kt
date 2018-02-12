package com.phaqlow.stag.persistence.dao

import com.phaqlow.stag.persistence.entity.Song
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class Songs(private val songDao: SongDao) {

    fun insertSong(song: Song): Single<Song> =
            Single.fromCallable {
                song.id = songDao.insertSong(song)
                song
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun deleteSong(song: Song): Single<Song> =
            Single.fromCallable {
                songDao.deleteSong(song)
                song
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun getAllSongs(): Single<List<Song>> =
            songDao.getAllSongs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}