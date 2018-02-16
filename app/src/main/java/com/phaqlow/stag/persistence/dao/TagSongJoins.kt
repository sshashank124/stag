package com.phaqlow.stag.persistence.dao

import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import com.phaqlow.stag.persistence.entity.TagSongJoin
import com.phaqlow.stag.util.ioToUiThread
import io.reactivex.Completable
import io.reactivex.Single


class TagSongJoins(private val tagSongJoinDao: TagSongJoinDao) {

    fun insertTagSongJoin(tagId: Long, songId: Long): Completable =
            Completable.fromAction {
                tagSongJoinDao.insertTagSongJoin(TagSongJoin(0, tagId, songId))
            }.ioToUiThread()

    fun deleteTagSongJoin(tagId: Long, songId: Long): Completable =
            Completable.fromAction {
                tagSongJoinDao.deleteTagSongJoin(tagId, songId)
            }.ioToUiThread()

    fun getSongsForTag(tag: Tag): Single<List<Song>> =
            tagSongJoinDao.getSongsForTag(tag.id).ioToUiThread()

    fun getTagsForSong(song: Song): Single<List<Tag>> =
            tagSongJoinDao.getTagsForSong(song.id).ioToUiThread()

    fun getSongsForTags(tags: List<Tag>): Single<List<Song>> =
            tagSongJoinDao.getSongsForTags(tags.map { tag -> tag.id }).ioToUiThread()

    fun getAllJoins(): Single<List<TagSongJoin>> =
            tagSongJoinDao.getAllJoins().ioToUiThread()
}