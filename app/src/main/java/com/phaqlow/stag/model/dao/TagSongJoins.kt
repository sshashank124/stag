package com.phaqlow.stag.model.dao

import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.TagSongJoin
import com.phaqlow.stag.util.ioToUi
import io.reactivex.Completable
import io.reactivex.Single


class TagSongJoins(private val tagSongJoinDao: TagSongJoinDao) {

    fun insertTagSongJoin(join: TagSongJoin): Completable =
            Completable.fromAction { tagSongJoinDao.insertTagSongJoin(join) }.ioToUi()

    private fun insertTagSongJoins(vararg joins: TagSongJoin): Completable =
            Completable.fromAction { tagSongJoinDao.insertTagSongJoins(*joins) }.ioToUi()

    fun insertTagSongJoins(joins: List<TagSongJoin>) = insertTagSongJoins(*joins.toTypedArray())

    fun deleteTagSongJoin(join: TagSongJoin): Completable =
            Completable.fromAction { tagSongJoinDao.deleteTagSongJoin(join) }.ioToUi()

    fun getSongsForTag(tag: Tag): Single<List<Song>> =
            tagSongJoinDao.getSongsForTag(tag.id).ioToUi()

    fun getTagsForSong(song: Song): Single<List<Tag>> =
            tagSongJoinDao.getTagsForSong(song.id).ioToUi()

    fun getSongsForTags(tags: List<Tag>): Single<List<Song>> =
            tagSongJoinDao.getSongsForTags(tags.map { it.id }).ioToUi()

    fun getAllTagsExceptWithSongs(songs: List<Song>): Single<List<Tag>> =
            tagSongJoinDao.getAllTagsExceptWithSongs(songs.map { it.id }, songs.size).ioToUi()
}