package com.phaqlow.stag

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.phaqlow.stag.model.dao.Songs
import com.phaqlow.stag.model.dao.TagSongJoins
import com.phaqlow.stag.model.dao.Tags
import com.phaqlow.stag.model.database.AppDatabase
import com.phaqlow.stag.model.entity.Song
import com.phaqlow.stag.model.entity.Tag
import com.phaqlow.stag.model.entity.TagSongJoin
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
    private lateinit var joins: TagSongJoins

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        tags = Tags(db.tagDao())
        songs = Songs(db.songDao())
        joins = TagSongJoins(db.tagSongJoinDao())
    }

    @Test
    fun t01_GetSongsForTag() {
        val tagId1 = tags.insertItem(Tag("T1")).blockingGet()
        val tagId2 = tags.insertItem(Tag("T2")).blockingGet()
        val songId1 = songs.insertItem(Song("S1")).blockingGet()
        val songId2 = songs.insertItem(Song("S2")).blockingGet()
        val songId3 = songs.insertItem(Song("S3")).blockingGet()

        joins.insertTagSongJoin(TagSongJoin(tagId1, songId1)).blockingAwait()
        joins.insertTagSongJoin(TagSongJoin(tagId2, songId2)).blockingAwait()
        joins.insertTagSongJoin(TagSongJoin(tagId1, songId3)).blockingAwait()

        assertThat(joins.getSongsForTag(Tag(tagId1)).blockingGet(),
                equalTo(listOf(Song(songId1), Song(songId3))))
    }

    @After
    fun teardown() {
        db.close()
    }
}
