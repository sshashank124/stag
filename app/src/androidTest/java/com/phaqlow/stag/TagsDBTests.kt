package com.phaqlow.stag

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.phaqlow.stag.persistence.dao.Songs
import com.phaqlow.stag.persistence.dao.TagSongJoins
import com.phaqlow.stag.persistence.dao.Tags
import com.phaqlow.stag.persistence.database.AppDatabase
import com.phaqlow.stag.persistence.entity.Song
import com.phaqlow.stag.persistence.entity.Tag
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class TagsDBTests {
    private lateinit var db: AppDatabase
    private lateinit var tags: Tags
    private lateinit var songs: Songs
    private lateinit var tagSongJoins: TagSongJoins

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        tags = Tags(db.tagDao())
        songs = Songs(db.songDao())
        tagSongJoins = TagSongJoins(db.tagSongJoinDao())
    }

    @Test
    fun t01_Add() {
        assertThat(tags.getAllTags().blockingGet(), equalTo(emptyList()))
        val tag1 = Tag(1, "tag1", null, 2)
        assertThat(tags.insertTag(tag1).blockingGet().name, equalTo(tag1.name))
    }

    @Test
    fun t10_GetSongsForTag() {
        val tagId1 = tags.insertTag(Tag("T1")).blockingGet().id
        val tagId2 = tags.insertTag(Tag("T2")).blockingGet().id
        val songId1 = songs.insertSong(Song("S1")).blockingGet().id
        val songId2 = songs.insertSong(Song("S2")).blockingGet().id
        val songId3 = songs.insertSong(Song("S3")).blockingGet().id

        tagSongJoins.insertTagSongJoin(tagId1, songId1).blockingAwait()
        tagSongJoins.insertTagSongJoin(tagId2, songId2).blockingAwait()
        tagSongJoins.insertTagSongJoin(tagId1, songId3).blockingAwait()

        assertThat(tagSongJoins.getSongsForTag(Tag(tagId1)).blockingGet(),
                equalTo(listOf(Song(songId1), Song(songId3))))
    }

    @After
    fun teardown() {
        db.close()
    }
}
