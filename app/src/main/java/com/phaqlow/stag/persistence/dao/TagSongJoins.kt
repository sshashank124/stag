package com.phaqlow.stag.persistence.dao

import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.persistence.entity.TagSongJoin
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


// TODO: add empty and error conditions to this class in case a DB operation fails (Maybe vs Single)
class TagSongJoins(private val tagSongJoinDao: TagSongJoinDao) {

    fun insertTagSongJoin(tagId: Long, songId: Long): Completable =
            Completable.fromAction {
                tagSongJoinDao.insertTagSongJoin(TagSongJoin(0, tagId, songId))
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun deleteTagSongJoin(tagId: Long, songId: Long): Completable =
            Completable.fromAction {
                tagSongJoinDao.deleteTagSongJoin(tagId, songId)
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    fun getSongsForTag(tag: Tag): Single<List<Song>> =
            tagSongJoinDao.getSongsForTag(tag.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getTagsForSong(song: Song): Single<List<Tag>> =
            tagSongJoinDao.getTagsForSong(song.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getSongsForTags(tags: List<Tag>): Single<List<Song>> =
            tagSongJoinDao.getSongsForTags(tags.map { tag -> tag.id })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    fun getAllJoins(): Single<List<TagSongJoin>> =
            tagSongJoinDao.getAllJoins()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}